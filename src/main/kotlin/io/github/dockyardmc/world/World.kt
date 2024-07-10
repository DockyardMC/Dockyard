package io.github.dockyardmc.world

import cz.lukynka.prettylog.log
import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.BindableList
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerChangeWorldEvent
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.DimensionType
import io.github.dockyardmc.runnables.AsyncRunnable
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.ChunkUtils
import io.github.dockyardmc.utils.Vector2
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.Vector3f
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

    var chunks: MutableList<Chunk> = mutableListOf()
    var defaultSpawnLocation = Location(0, 0, 0, this)

    val players: BindableList<Player> = BindableList()
    val entities: BindableList<Entity> = BindableList()

    var canBeJoined: Bindable<Boolean> = Bindable(false)
    val joinQueue: MutableList<Player> = mutableListOf()

    fun join(player: Player) {
        if(player.world == this && player.isFullyInitialized) return
        if(!canBeJoined.value && !joinQueue.contains(player)) {
            joinQueue.addIfNotPresent(player)
            log("$player joined before world $name is loaded, added to joinQueue")
            return
        }

        log("Logged in $player")
        val oldWorld = player.world

        player.world.players.removeIfPresent(player)
        player.world = this
        players.add(player)
        entities.add(player)

        Events.dispatch(PlayerChangeWorldEvent(player, oldWorld, this))

        joinQueue.removeIfPresent(player)
        player.respawn()

        player.isFullyInitialized = true
    }

    init {

        val runnable = AsyncRunnable {
            generateChunks(6)
        }

        runnable.callback = {
            log("World $name is read to be joined!")
            log("Joining following players: $joinQueue")
            canBeJoined.value = true
            joinQueue.forEach(::join)
        }
        runnable.execute()

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

    fun getChunk(x: Int, z: Int): Chunk? = chunks.firstOrNull { it.chunkX == x && it.chunkZ == z }

    fun setBlock(x: Int, y: Int, z: Int, block: Block) {
        val chunk = getChunkAt(x, z) ?: return
        chunk.setBlock(x, y, z, block, true)
        players.values.forEach { it.sendPacket(chunk.packet) }
    }

    fun getBlock(x: Int, y: Int, z: Int): Block {
        val chunk = getChunkAt(x, z) ?: return Blocks.AIR
        return chunk.getBlock(x, y, z)
    }

    fun getBlock(location: Location): Block = this.getBlock(location.x.toInt(), location.y.toInt(), location.z.toInt())

    fun getBlock(vector: Vector3f): Block = this.getBlock(vector.x.toInt(), vector.y.toInt(), vector.z.toInt())

    fun getBlock(vector: Vector3): Block = this.getBlock(vector.x, vector.y, vector.z)

    fun setBlock(location: Location, block: Block) {
        this.setBlock(location.x.toInt(), location.y.toInt(), location.z.toInt(), block)
    }

    fun setBlock(vector3: Vector3, block: Block) {
        this.setBlock(vector3.x, vector3.y, vector3.z, block)
    }

    fun setBlock(vector3: Vector3f, block: Block) {
        this.setBlock(vector3.x.toInt(), vector3.y.toInt(), vector3.z.toInt(), block)
    }

    fun generateChunks(size: Int) {
        val vector = Vector2(size.toFloat(), size.toFloat())
        for (chunkX in (vector.x.toInt() * -1)..vector.x.toInt()) {
            for (chunkZ in (vector.y.toInt() * -1)..vector.y.toInt()) {
                val chunk = getChunk(chunkX, chunkZ) ?: Chunk(chunkX, chunkZ, this)
                for (localX in 0..<16) {
                    for (localZ in 0..<16) {
                        val worldX = chunkX * 16 + localX
                        val worldZ = chunkZ * 16 + localZ

                        for (y in 0..<dimensionType.height) {
                            chunk.setBlock(localX, y, localZ, generator.getBlock(worldX, y, worldZ), false)
                            chunk.setBiome(localX, y, localZ, generator.getBiome(worldX, y, worldZ))
                        }
                    }
                }
                if(getChunk(chunkX, chunkZ) == null) chunks.add(chunk)
                chunk.cacheChunkDataPacket()
            }
        }
    }

    fun getRandom(): Random = Random(seed)
}