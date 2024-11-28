package io.github.dockyardmc.world

import cz.lukynka.Bindable
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.blocks.BatchBlockUpdate
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityTeleportPacket
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.registries.BlockRegistry
import io.github.dockyardmc.registry.registries.DimensionType
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.scheduler.Scheduler
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.ChunkUtils
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.utils.vectors.Vector2f
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.world.WorldManager.mainWorld
import io.github.dockyardmc.world.chunk.Chunk
import io.github.dockyardmc.world.chunk.ChunkPos
import io.github.dockyardmc.world.generators.VoidWorldGenerator
import io.github.dockyardmc.world.generators.WorldGenerator
import java.util.*

class World(var name: String, var generator: WorldGenerator, var dimensionType: DimensionType) : Disposable {

    var eventPool = EventPool()
    val scheduler = Scheduler("${name}_world_scheduler")

    init {
        if (name.hasUpperCase()) throw IllegalArgumentException("World name cannot contain uppercase characters")

        eventPool.on<ServerTickEvent> {
            scheduler.tick(it.serverTicks)
        }

        scheduler.runRepeating(1.ticks) {
            tick()
        }
    }

    val worldSeed = UUID.randomUUID().leastSignificantBits.toString()
    var seed: Long = worldSeed.SHA256Long()

    val difficulty: Bindable<Difficulty> = Bindable(Difficulty.NORMAL)
    var worldBorder = WorldBorder(this)

    var time: Bindable<Long> = Bindable(1000L)
    var worldAge: Long = 0

    var chunks: MutableMap<Long, Chunk> = mutableMapOf()
    var defaultSpawnLocation = Location(0, 0, 0, this)

    private val innerPlayers: MutableList<Player> = mutableListOf()
    private val innerEntities: MutableList<Entity> = mutableListOf()

    val players get() = innerPlayers.toList()
    val entities get() = innerEntities.toList()

    var canBeJoined: Bindable<Boolean> = Bindable(false)
    val joinQueue: MutableList<Player> = mutableListOf()

    var isHardcore: Boolean = false
    var freezeTime: Boolean = false

    var seaLevel = 0

    val customDataBlocks: MutableMap<Int, Block> = mutableMapOf()

    fun tick() {
        // tick entities
        synchronized(entities) {
            scheduler.run {
                entities.forEach {
                    if (it.tickable) it.tick()
                }
            }
        }
    }

    fun addEntity(entity: Entity) {
        synchronized(innerEntities) {
            innerEntities.add(entity)
        }
    }

    fun removeEntity(entity: Entity) {
        synchronized(innerEntities) {
            innerEntities.remove(entity)
        }
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
        if (!canBeJoined.value && !joinQueue.contains(player)) {
            joinQueue.addIfNotPresent(player)
            debug("$player joined before world $name is loaded, added to joinQueue", logType = LogType.DEBUG)
            return
        }

        val oldWorld = player.world

        oldWorld.entities.filter { it != player }.forEach { it.removeViewer(player) }
        oldWorld.players.filter { it != player }.forEach {
            it.removeViewer(player)
            player.removeViewer(it)
        }

        player.world.innerPlayers.removeIfPresent(player)
        player.world = this

        addEntity(player)
        addPlayer(player)

        Events.dispatch(PlayerChangeWorldEvent(player, oldWorld, this))

        joinQueue.removeIfPresent(player)
        player.respawn()
        player.sendPacketToViewers(ClientboundEntityTeleportPacket(player, player.location))

        player.isFullyInitialized = true
        player.updateWorldTime()
    }

    fun generate(then: ((World) -> Unit)? = null) {
        val task = scheduler.runAsync {
            generateBaseChunks(6)
        }
        task.thenAccept {
            log("World $name has finished loading!", LogType.RUNTIME)
            canBeJoined.value = true
            joinQueue.forEach(::join)
            Events.dispatch(WorldFinishLoadingEvent(this))
            then?.invoke(this)
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
    }

    fun sendMessage(message: String) {
        this.sendMessage(message.toComponent())
    }

    fun sendMessage(component: Component) {
        players.sendMessage(component)
    }

    fun sendActionBar(message: String) {
        this.sendActionBar(message.toComponent())
    }

    fun sendActionBar(component: Component) {
        players.sendActionBar(component)
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
        synchronized(chunks) {
            return chunks[ChunkUtils.getChunkIndex(x, z)]
        }
    }

    fun setBlock(x: Int, y: Int, z: Int, block: RegistryBlock) {
        setBlock(x, y, z, block.toBlock())
    }

    fun setBlock(x: Int, y: Int, z: Int, block: Block) {
        val chunk = getChunkAt(x, z) ?: return
        chunk.setBlock(x, y, z, block, true)
        players.forEach { it.sendPacket(chunk.packet) }
    }

    fun getBlock(location: Location): Block =
        this.getBlock(location.x.toInt(), location.y.toInt(), location.z.toInt())

    fun getBlock(vector3: Vector3): Block =
        this.getBlock(vector3.x, vector3.y, vector3.z)

    fun getBlock(x: Int, y: Int, z: Int): Block {
        val chunk = getChunkAt(x, z) ?: throw IllegalStateException("Chunk at $x, $z not generated!")
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

    fun setBlock(location: Location, registrBlock: RegistryBlock) {
        this.setBlock(location, registrBlock.toBlock())
    }

    fun setBlockRaw(location: Location, blockStateId: Int, updateChunk: Boolean = true) {
        setBlockRaw(location.x.toInt(), location.y.toInt(), location.z.toInt(), blockStateId, updateChunk)
    }

    fun setBlockRaw(x: Int, y: Int, z: Int, blockStateId: Int, updateChunk: Boolean = true) {
        val chunk = getChunkAt(x, z) ?: return
        chunk.setBlockRaw(x, y, z, blockStateId, updateChunk)
        if (updateChunk) players.forEach { it.sendPacket(chunk.packet) }
    }

    fun batchBlockUpdate(builder: BatchBlockUpdate.() -> Unit) {
        val update = BatchBlockUpdate(this)
        builder.invoke(update)

        val runnable = scheduler.runAsync {
            val chunks: MutableList<Chunk> = mutableListOf()
            update.updates.forEach { (location, block) ->
                val chunk = getOrGenerateChunk(
                    ChunkUtils.getChunkCoordinate(location.x),
                    ChunkUtils.getChunkCoordinate(location.z)
                )
                if (!chunks.contains(chunk)) chunks.add(chunk)

                setBlockRaw(location, block.getProtocolId(), false)
            }
            chunks.forEach { chunk ->
                chunk.updateCache()
                this@World.players.sendPacket(chunk.packet)
            }
        }
        runnable.thenAccept {
            update.then?.invoke()
        }
    }

    fun fill(from: Location, to: Location, block: RegistryBlock, thenRun: (() -> Unit)? = null) {
        fill(from, to, block, thenRun)
    }

    fun fill(from: Location, to: Location, block: Block, thenRun: (() -> Unit)? = null) {
        batchBlockUpdate {
            fill(from, to, block)
            then = thenRun
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
        if (generator is VoidWorldGenerator) {
            chunk.sections.forEach { section ->
                section.biomePalette.fill(Biomes.THE_VOID.getProtocolId())
                section.blockPalette.fill(BlockRegistry.Air.defaultBlockStateId)
            }
        } else {
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

    fun getRandom(): Random = Random(seed)

    override fun dispose() {
        players.forEach {
            it.teleport(mainWorld.defaultSpawnLocation)
            innerPlayers.remove(it)
        }
        entities.forEach {
            if (it is Player) return@forEach
            despawnEntity(it)
        }
        customDataBlocks.clear()
        canBeJoined.value = false
        chunks.clear()

        WorldManager.worlds.remove(this.name)
        scheduler.dispose()
        eventPool.dispose()
    }
}