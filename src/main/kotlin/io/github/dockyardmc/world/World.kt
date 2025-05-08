package io.github.dockyardmc.world

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindablePool
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.LightningBolt
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.SHA256Long
import io.github.dockyardmc.extentions.addIfNotPresent
import io.github.dockyardmc.extentions.hasUpperCase
import io.github.dockyardmc.extentions.removeIfPresent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector2f
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.particles.BlockParticleData
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityTeleportPacket
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.registries.BlockRegistry
import io.github.dockyardmc.registry.registries.DimensionType
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.scheduler.CustomRateScheduler
import io.github.dockyardmc.scheduler.runLaterAsync
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.UsedAfterDisposedException
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.utils.getWorldEventContext
import io.github.dockyardmc.world.WorldManager.mainWorld
import io.github.dockyardmc.world.block.BatchBlockUpdate
import io.github.dockyardmc.world.block.Block
import io.github.dockyardmc.world.chunk.Chunk
import io.github.dockyardmc.world.chunk.ChunkPos
import io.github.dockyardmc.world.chunk.ChunkUtils
import io.github.dockyardmc.world.generators.VoidWorldGenerator
import io.github.dockyardmc.world.generators.WorldGenerator
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import java.util.*
import java.util.concurrent.CompletableFuture

class World(var name: String, var generator: WorldGenerator, var dimensionType: DimensionType) : Disposable {

    val eventPool = EventPool()
    private val bindablePool = BindablePool()
    val scheduler = CustomRateScheduler("${name}_world_scheduler")
    val uuid = UUID.randomUUID()

    val worldSeed = UUID.randomUUID().leastSignificantBits.toString()
    var seed: Long = worldSeed.SHA256Long()

    val difficulty: Bindable<Difficulty> = bindablePool.provideBindable(Difficulty.NORMAL)
    val worldBorder = WorldBorder(this)
    val weather: Bindable<Weather> = bindablePool.provideBindable(Weather.CLEAR)

    val time: Bindable<Long> = bindablePool.provideBindable(1000L)
    var worldAge: Long = 0

    val chunks: Long2ObjectOpenHashMap<Chunk> = Long2ObjectOpenHashMap()
    var defaultSpawnLocation = Location(0, 0, 0, this)

    private val innerPlayers: MutableList<Player> = mutableListOf()
    private val innerEntities: MutableList<Entity> = mutableListOf()

    val players get() = innerPlayers.toList()
    val entities get() = innerEntities.toList()

    val isLoaded: Bindable<Boolean> = bindablePool.provideBindable(false)
    val playerJoinQueue: MutableList<Player> = mutableListOf()

    var isHardcore: Boolean = false
    var freezeTime: Boolean = false

    var seaLevel = 0

    val customDataBlocks: MutableMap<Int, Block> = mutableMapOf()

    init {
        if (name.hasUpperCase()) throw IllegalArgumentException("World name cannot contain uppercase characters")

        scheduler.syncWithGlobalScheduler()
        scheduler.runRepeating(1.ticks) {
            tick()
        }

        weather.valueChanged { _ ->
            players.forEach { player ->
                runLaterAsync(1.ticks) {
                    player.updateWeatherState()
                }
            }
        }
    }

    fun schedule(unit: (World) -> Unit) {
        if (isLoaded.value) {
            unit.invoke(this)
        } else {
            isLoaded.valueChangedThenSelfDispose { event ->
                if (event.newValue) unit.invoke(this) else throw UsedAfterDisposedException(this)
            }
        }
    }

    fun strikeLightning(location: Location) {
        val entity = LightningBolt(location)
        spawnEntity(entity)
        scheduler.runLater(10.ticks) {
            despawnEntity(entity)
        }
    }

    fun tick() {
        if (!isLoaded.value) return
        val event = WorldTickEvent(this, scheduler, getWorldEventContext(this))
        Events.dispatch(event)

        if (event.cancelled) return

        // tick entities
        scheduler.run {
            synchronized(innerEntities) {
                entities.forEach {
                    if (it.tickable) it.tick()
                }
            }
        }
    }

    fun addEntity(entity: Entity) {
        innerEntities.add(entity)
    }

    fun removeEntity(entity: Entity) {
        innerEntities.remove(entity)
    }

    fun removePlayer(entity: Entity) {
        synchronized(innerPlayers) {
            innerPlayers.remove(entity)
        }
    }

    fun addPlayer(player: Player) {
        synchronized(innerPlayers) {
            innerPlayers.add(player)
        }
    }

    fun join(player: Player) {
        if (player.world == this && player.isFullyInitialized) return
        if (!isLoaded.value && !playerJoinQueue.contains(player)) {
            playerJoinQueue.addIfNotPresent(player)
            debug("$player joined before world $name is loaded, added to joinQueue", logType = LogType.DEBUG)
            return
        }

        val oldWorld = player.world

        player.world.innerPlayers.removeIfPresent(player)
        player.world = this

        player.entityViewSystem.lock.lock()

        player.viewers.toList().forEach { viewer ->
            viewer.removeViewer(player)
            player.removeViewer(viewer)
        }

        player.entityViewSystem.clear()

        addEntity(player)
        addPlayer(player)

        Events.dispatch(PlayerChangeWorldEvent(player, oldWorld, this))

        playerJoinQueue.removeIfPresent(player)

        player.entityViewSystem.lock.unlock()

        player.respawn()
        player.entityViewSystem.tick()
        player.sendPacketToViewers(ClientboundEntityTeleportPacket(player, player.location))

        player.isFullyInitialized = true
        player.updateWorldTime()
    }

    fun load(): CompletableFuture<Unit> {
        val task = scheduler.runAsync {
            if (generator.generateBaseChunks) {
                generateBaseChunks(6)
            }
            generator.onWorldLoad(this)
        }

        task.thenAccept {
            log("World $name has finished loading!", WorldManager.LOG_TYPE)
            isLoaded.value = true
            playerJoinQueue.forEach(::join)
            val event = WorldFinishLoadingEvent(this)
            Events.dispatch(event)
        }

        time.valueChanged {
            innerPlayers.forEach { player ->
                player.updateWorldTime()
            }
        }

        eventPool.on<ServerTickEvent> {
            worldAge++
            if (freezeTime) {
                if (worldAge % 5L == 0L) {
                    time.triggerUpdate()
                }
            } else {
                time.setSilently(time.value + 1)
            }
        }
        return task
    }

    fun getChunkAt(x: Int, z: Int): Chunk? {
        val chunkX = ChunkUtils.getChunkCoordinate(x)
        val chunkZ = ChunkUtils.getChunkCoordinate(z)
        return getChunk(chunkX, chunkZ)
    }

    fun getChunkAt(location: Location): Chunk? = getChunkAt(location.x.toInt(), location.z.toInt())

    fun getChunk(pos: ChunkPos): Chunk? {
        return getChunk(pos.x, pos.z)
    }

    fun getChunk(x: Int, z: Int): Chunk? {
        return chunks[ChunkUtils.getChunkIndex(x, z)]
    }

    fun destroyNaturally(vector: Vector3) {
        destroyNaturally(vector.toLocation(this))
    }

    fun destroyNaturally(x: Int, y: Int, z: Int) {
        destroyNaturally(Location(x, y, z, this))
    }

    fun destroyNaturally(location: Location) {
        val block = location.block
        if (block.isAir()) return
        setBlock(location, Blocks.AIR)
        players.playSound(block.registryBlock.sounds.breakSound, location)
        players.spawnParticle(
            location.add(0.5, 0.5, 0.5),
            Particles.BLOCK,
            amount = 35,
            offset = Vector3f(0.3f),
            particleData = BlockParticleData(block)
        )
    }

    fun setBlock(x: Int, y: Int, z: Int, block: RegistryBlock) {
        setBlock(x, y, z, block.toBlock())
    }

    fun setBlock(x: Int, y: Int, z: Int, block: Block) {
        val chunk = getChunkAt(x, z) ?: throw IllegalStateException("Chunk at $x, $y is does not exist!")
        chunk.setBlock(x, y, z, block, true)
        chunk.sendUpdateToViewers()
    }

    fun getBlock(location: Location): Block = this.getBlock(location.x.toInt(), location.y.toInt(), location.z.toInt())

    fun getBlock(vector3: Vector3): Block = this.getBlock(vector3.x, vector3.y, vector3.z)

    fun getBlock(x: Int, y: Int, z: Int): Block {
        val chunk = getChunkAt(x, z) ?: throw IllegalStateException("Chunk at $x, $z does not exist!")
        return chunk.getBlock(x, y, z)
    }

    fun setBlockState(x: Int, y: Int, z: Int, states: Map<String, String>) {
        val location = Location(x, y, z, this)
        val existingBlock = getBlock(location)
        val existingBlockStates = existingBlock.blockStates
        val newStates = mutableMapOf<String, String>()
        newStates.putAll(existingBlockStates)
        states.forEach { newStates[it.key] = it.value }

        setBlock(location, Block(existingBlock.registryBlock, newStates, existingBlock.customData))
    }

    fun setBlockState(x: Int, y: Int, z: Int, vararg states: Pair<String, String>) {
        setBlockState(x, y, z, states.toMap())
    }

    fun setBlockState(location: Location, states: Map<String, String>) {
        setBlockState(location.x.toInt(), location.y.toInt(), location.z.toInt(), states)
    }

    fun setBlockState(location: Location, vararg states: Pair<String, String>) {
        setBlockState(location.x.toInt(), location.y.toInt(), location.z.toInt(), states.toMap())
    }

    fun setBlock(location: Location, block: Block) {
        this.setBlock(location.x.toInt(), location.y.toInt(), location.z.toInt(), block)
    }

    fun setBlock(location: Location, registryBlock: RegistryBlock) {
        this.setBlock(location, registryBlock.toBlock())
    }

    fun setBlock(vector: Vector3, registryBlock: RegistryBlock) {
        this.setBlock(vector, registryBlock.toBlock())
    }

    fun setBlock(vector: Vector3, block: Block) {
        this.setBlock(vector.toLocation(this), block)
    }

    fun setBlockRaw(location: Location, blockStateId: Int, updateChunk: Boolean = true) {
        setBlockRaw(location.x.toInt(), location.y.toInt(), location.z.toInt(), blockStateId, updateChunk)
    }

    fun setBlockRaw(x: Int, y: Int, z: Int, blockStateId: Int, updateChunk: Boolean = true) {
        val chunk = getChunkAt(x, z) ?: return
        chunk.setBlockRaw(x, y, z, blockStateId, updateChunk)
        if (updateChunk) chunk.sendUpdateToViewers()
    }

    fun batchBlockUpdate(builder: BatchBlockUpdate.() -> Unit): CompletableFuture<World> {
        if (!isLoaded.value) throw IllegalStateException("World has not been fully loaded yet! Please use World#schedule or wait until world is fully loaded")
        val update = BatchBlockUpdate(this)
        builder.invoke(update)
        val future = CompletableFuture<World>()

        val runnable = scheduler.runAsync {
            val chunks: MutableList<Chunk> = mutableListOf()
            update.updates.forEach { (location, block) ->
                val chunk = getOrGenerateChunk(
                    ChunkUtils.getChunkCoordinate(location.x), ChunkUtils.getChunkCoordinate(location.z)
                )
                if (!chunks.contains(chunk)) chunks.add(chunk)

                setBlockRaw(location, block.getProtocolId(), false)
            }
            chunks.forEach { chunk ->
                chunk.updateCache()
                chunk.sendUpdateToViewers()
            }
        }
        runnable.thenAccept {
            future.complete(this)
        }

        return future
    }

    fun fill(from: Location, to: Location, block: RegistryBlock): CompletableFuture<World> {
        return fill(from, to, block)
    }

    fun fill(from: Location, to: Location, block: Block): CompletableFuture<World> {
        return batchBlockUpdate {
            fill(from, to, block)
        }
    }

    fun getOrGenerateChunk(pos: ChunkPos): Chunk {
        return getOrGenerateChunk(pos.x, pos.z)
    }

    fun getOrGenerateChunk(x: Int, z: Int): Chunk {
        val chunk = getChunk(x, z)
        if (chunk == null) {
            generateChunk(x, z)
            return getChunk(x, z)!!
        } else {
            return chunk
        }
    }

    fun generateChunk(pos: ChunkPos): Chunk {
        return generateChunk(pos.x, pos.z)
    }

    fun generateChunk(x: Int, z: Int): Chunk {
        val chunk = getChunk(x, z) ?: Chunk(x, z, this)
        // Special case for void world generator for fast void world loading. //TODO optimizations to rest of the world generators
        when (generator) {
            is VoidWorldGenerator -> {
                chunk.sections.forEach { section ->
                    section.biomePalette.fill((generator as VoidWorldGenerator).defaultBiome.getProtocolId())
                    section.blockPalette.fill(BlockRegistry.Air.defaultBlockStateId)
                }
            }

            else -> {
                for (localX in 0..<16) {
                    for (localZ in 0..<16) {
                        val worldX = x * 16 + localX
                        val worldZ = z * 16 + localZ

                        for (y in 0..<dimensionType.height) {
                            chunk.setBlock(localX, y, localZ, generator.getBlock(worldX, y, worldZ), false)
                            chunk.setBiome(localX, y, localZ, generator.getBiome(worldX, y, worldZ), false)
                        }
                    }
                }
            }
        }
        chunk.updateCache()
        if (getChunk(x, z) == null) {
            synchronized(chunks) {
                chunks[ChunkUtils.getChunkIndex(x, z)] = (chunk)
            }
        }
        return chunk
    }

    fun generateBaseChunks(size: Int) {
        val vector = Vector2f(size.toFloat(), size.toFloat())
        ((vector.x.toInt() * -1)..vector.x.toInt()).forEach chunkLoop@{ chunkX ->
            for (chunkZ in (vector.y.toInt() * -1)..vector.y.toInt()) {
                generateChunk(chunkX, chunkZ)
            }
        }
    }

    fun locationAt(x: Int, y: Int, z: Int): Location {
        return Location(x, y, z, this)
    }

    fun locationAt(vector: Vector3): Location {
        return Location(vector.x, vector.y, vector.z, this)
    }

    fun locationAt(vector: Vector3d): Location {
        return Location(vector.x, vector.y, vector.z, this)
    }

    fun locationAt(vector: Vector3f): Location {
        return Location(vector.x, vector.y, vector.z, this)
    }

    fun getRandom(): Random = Random(seed)

    override fun dispose() {
        players.forEach { player ->
            player.teleport(mainWorld.defaultSpawnLocation)
            innerPlayers.remove(player)
        }
        entities.forEach { entity ->
            if (entity is Player) return@forEach
            despawnEntity(entity)
        }
        customDataBlocks.clear()
        isLoaded.value = false
        chunks.clear()

        WorldManager.worlds.remove(this.name)
        scheduler.dispose()
        eventPool.dispose()
    }
}