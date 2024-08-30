package io.github.dockyardmc.world

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.entities.EntityManager.despawnEntity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerChangeWorldEvent
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.events.WorldFinishLoadingEvent
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.runnables.AsyncQueueProcessor
import io.github.dockyardmc.runnables.AsyncQueueTask
import io.github.dockyardmc.runnables.runLaterAsync
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.*
import io.github.dockyardmc.world.WorldManager.mainWorld
import io.github.dockyardmc.world.generators.VoidWorldGenerator
import io.github.dockyardmc.world.generators.WorldGenerator
import java.util.Random
import java.util.UUID

class World(
    var name: String,
    var generator: WorldGenerator,
    var dimensionType: DimensionType
) {
    val worldSeed = UUID.randomUUID().leastSignificantBits.toString()

    val difficulty: Bindable<Difficulty> = Bindable(Difficulty.NORMAL)

    var seed: Long = worldSeed.SHA256Long()
    var worldBorder = WorldBorder(this)

    var time: Long = 1000
    var worldAge: Long = 0

    var chunks: MutableMap<Long, Chunk> = mutableMapOf()
    var defaultSpawnLocation = Location(0, 0, 0, this)

    val players: BindableList<Player> = BindableList()
    val entities: BindableList<Entity> = BindableList()

    var canBeJoined: Bindable<Boolean> = Bindable(false)
    val joinQueue: MutableList<Player> = mutableListOf()

    var isHardcore: Boolean = false

    var asyncChunkGenerator = AsyncQueueProcessor()

    fun join(player: Player) {
        if(player.world == this && player.isFullyInitialized) return
        if(!canBeJoined.value && !joinQueue.contains(player)) {
            joinQueue.addIfNotPresent(player)
            debug("$player joined before world $name is loaded, added to joinQueue", LogType.DEBUG)
            return
        }

        val oldWorld = player.world

        oldWorld.entities.values.filter { it != player }.forEach { it.removeViewer(player, false) }
        oldWorld.players.values.filter { it != player }.forEach {
            it.removeViewer(player, false)
            player.removeViewer(it, false)
        }

        player.world.players.removeIfPresent(player)
        player.world = this
        players.add(player)
        entities.add(player)

        Events.dispatch(PlayerChangeWorldEvent(player, oldWorld, this))

        joinQueue.removeIfPresent(player)
        player.respawn()
        player.chunkEngine.loadedChunks.clear()

        runLaterAsync(2) {
            players.values.filter { it != player }.forEach {
                it.addViewer(player)
                player.addViewer(it)
            }
        }

        entities.values.filter { it != player && it !is Player }.forEach { it.addViewer(player) }

        player.isFullyInitialized = true
    }

    init {
        val runnable = AsyncQueueTask("generate-base-chunks") {
            generateBaseChunks(6)
        }

        runnable.callback = {
            log("World $name has finished loading!", LogType.RUNTIME)
            canBeJoined.value = true
            joinQueue.forEach(::join)
            Events.dispatch(WorldFinishLoadingEvent(this))
        }
        asyncChunkGenerator.submit(runnable)

        Events.on<ServerTickEvent> {
            worldAge++
        }
    }

    fun sendMessage(message: String) { this.sendMessage(message.toComponent()) }
    fun sendMessage(component: Component) { players.values.sendMessage(component) }
    fun sendActionBar(message: String) { this.sendActionBar(message.toComponent()) }
    fun sendActionBar(component: Component) { players.values.sendActionBar(component) }

    fun getChunkAt(x: Int, z: Int): Chunk? {
        val chunkX = ChunkUtils.getChunkCoordinate(x)
        val chunkZ = ChunkUtils.getChunkCoordinate(z)
        return getChunk(chunkX, chunkZ)
    }

    fun getChunkAt(location: Location): Chunk? = getChunkAt(location.x.toInt(), location.z.toInt())

    fun getChunkFromIndex(index: Long): Chunk? = chunks[index]

    fun getChunk(x: Int, z: Int): Chunk? = chunks[ChunkUtils.getChunkIndex(x, z)]

    fun setBlock(x: Int, y: Int, z: Int, block: Block) {
        val chunk = getChunkAt(x, z) ?: return
        chunk.setBlock(x, y, z, block, true)
        players.values.forEach { it.sendPacket(chunk.packet) }
    }

    fun getBlock(x: Int, y: Int, z: Int): Block {
        val chunk = getChunkAt(x, z) ?: return Blocks.AIR
        return chunk.getBlock(x, y, z)
    }

    fun setBlockState(x: Int, y: Int, z: Int, states: Map<String, String>) {
        val location = Location(x, y, z, this)
        val existingBlock = getBlock(location)
        val existingBlockStates = existingBlock.blockStates
        val newStates = mutableMapOf<String, String>()
        newStates.putAll(existingBlockStates)
        states.forEach { newStates[it.key] = it.value }

        setBlock(location, existingBlock.withBlockStates(newStates))
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

    fun getBlock(location: Location): Block = this.getBlock(location.x.toInt(), location.y.toInt(), location.z.toInt())

    fun getBlock(vector: Vector3f): Block = this.getBlock(vector.x.toInt(), vector.y.toInt(), vector.z.toInt())

    fun getBlock(vector: Vector3): Block = this.getBlock(vector.x, vector.y, vector.z)

    fun setBlock(location: Location, block: Block) {
        this.setBlock(location.x.toInt(), location.y.toInt(), location.z.toInt(), block)
    }

    fun setBlockRaw(location: Location, blockStateId: Int, updateChunk: Boolean = true) {
        val chunk = getChunkAt(location.x.toInt(), location.z.toInt()) ?: return
        chunk.setBlockRaw(location.x.toInt(), location.y.toInt(), location.z.toInt(), blockStateId)
        if(updateChunk) players.values.forEach { it.sendPacket(chunk.packet) }
    }

    fun setBlock(vector3: Vector3, block: Block) {
        this.setBlock(vector3.x, vector3.y, vector3.z, block)
    }

    fun setBlock(vector3: Vector3f, block: Block) {
        this.setBlock(vector3.x.toInt(), vector3.y.toInt(), vector3.z.toInt(), block)
    }

    fun generateChunk(x: Int, z: Int) {
        val chunk = getChunk(x, z) ?: Chunk(x, z, this)
        // Special case for void world generator for fast void world loading. //TODO optimizations to rest of the world generators
        if(generator is VoidWorldGenerator) {
            chunk.sections.forEach { section ->
                section.biomePalette.fill(Biomes.THE_VOID.id)
                section.blockPalette.fill(Blocks.AIR.getId())
            }
        } else {
            for (localX in 0..<16) {
                for (localZ in 0..<16) {
                    val worldX = x * 16 + localX
                    val worldZ = z * 16 + localZ

                    for (y in 0..<dimensionType.height) {
                        chunk.setBlock(localX, y, localZ, generator.getBlock(worldX, y, worldZ), false)
                        chunk.setBiome(localX, y, localZ, generator.getBiome(worldX, y, worldZ))
                    }
                }
            }
        }
//        chunk.cacheChunkDataPacket()
        if(getChunk(x, z) == null) chunks[ChunkUtils.getChunkIndex(x, z)] = (chunk)
    }

    fun generateBaseChunks(size: Int) {
        val vector = Vector2f(size.toFloat(), size.toFloat())
        ((vector.x.toInt() * -1)..vector.x.toInt()).forEach chunkLoop@{ chunkX ->
            for (chunkZ in (vector.y.toInt() * -1)..vector.y.toInt()) {
                generateChunk(chunkX, chunkZ)
            }
        }
    }

    fun delete() {
        players.values.forEach {
            it.teleport(mainWorld.defaultSpawnLocation)
            players.remove(it)
        }
        entities.values.forEach {
            if(it is Player) return@forEach
            despawnEntity(it)
            entities.remove(it)
        }
        canBeJoined.value = false
        chunks.clear()

        WorldManager.worlds.remove(this.name)
        this.asyncChunkGenerator.shutdown()
    }

    fun getRandom(): Random = Random(seed)
}