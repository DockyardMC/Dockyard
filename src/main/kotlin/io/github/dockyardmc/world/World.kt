package io.github.dockyardmc.world

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.ChunkUtils
import io.github.dockyardmc.utils.Vector2
import io.github.dockyardmc.world.generators.FlatWorldGenerator
import io.github.dockyardmc.world.generators.VanillaIshWorldGenerator
import io.github.dockyardmc.world.generators.WorldGenerator
import java.util.UUID
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

class World(
    var name: String = "world",
    var dimensionType: DimensionType = DimensionType.OVERWORLD,
    worldSeed: String = "trans rights!!"
) {

    var seed: Long = worldSeed.SHA256Long()
    var seedBytes = worldSeed.SHA256String()
    var worldBorder = WorldBorder(this)
    var generator: WorldGenerator = FlatWorldGenerator(this)

    var daylightCycle: Boolean = true
    var time: Long = 1000
    var worldAge: Long = 0

    var chunks: MutableList<Chunk> = mutableListOf()

    val players: MutableList<Player> get() = PlayerManager.players.filter { it.world == this }.toMutableList()

    var defaultSpawnLocation = Location(0, 0, 0)

    fun sendMessage(message: String) { this.sendMessage(message.toComponent()) }
    fun sendMessage(component: Component) { players.sendMessage(component) }
    fun sendActionBar(message: String) { this.sendActionBar(message.toComponent()) }
    fun sendActionBar(component: Component) { players.sendActionBar(component) }


    fun getChunkAt(x: Int, z: Int): Chunk? {
        val chunkX = ChunkUtils.getChunkCoordinate(x)
        val chunkZ = ChunkUtils.getChunkCoordinate(z)
        return getChunk(chunkX, chunkZ)
    }

    fun getChunk(x: Int, z: Int): Chunk? {
        return chunks.firstOrNull { it.chunkX == x && it.chunkZ == z }
    }

    fun generateChunks(size: Int, sendToPlayers: Boolean = false) {
        val vector = Vector2(size.toFloat(), size.toFloat())
        for (chunkX in (vector.x.toInt() * -1)..vector.x.toInt()) {
            for (chunkZ in (vector.y.toInt() * -1)..vector.y.toInt()) {
                val chunk = getChunk(chunkX, chunkZ) ?: Chunk(chunkX, chunkZ, this)
                for (localX in 0..<16) {
                    for (localZ in 0..<16) {
                        val worldX = chunkX * 16 + localX
                        val worldZ = chunkZ * 16 + localZ

                        for (y in 0..<256) {
                            chunk.setBlock(localX, y, localZ, generator.getBlock(worldX, y, worldZ))
                            chunk.setBiome(localX, y, localZ, generator.getBiome(worldX, y, worldZ))
                        }
                    }
                }
                if(getChunk(chunkX, chunkZ) == null) chunks.add(chunk)
                chunk.cacheChunkDataPacket()
                if(sendToPlayers) {
                    PlayerManager.players.forEach { it.sendPacket(chunk.packet) }
                }
            }
        }
    }

    init {

        generateChunks(6)

        // Time
        Events.on<ServerTickEvent> {
            worldAge++
            if(!daylightCycle) return@on
            if(time > 24000) {
                time = 0
            } else {
                time++
            }
        }
    }
}

enum class DimensionType(val maxY: Int, val minY: Int) {
    OVERWORLD(320, -64),
    NETHER(255, 0),
    THE_END(255, 0)
}