package io.github.dockyardmc.world

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.Vector2

class World(var name: String = "world", var dimensionType: DimensionType = DimensionType.OVERWORLD, worldSeed: String = "trans rights!!") {

    var seed = worldSeed.SHA256Long()
    var seedBytes = worldSeed.SHA256String()
    var worldBorder = WorldBorder(this)

    var daylightCycle: Boolean = true
    var time: Long = 1000
    var worldAge: Long = 0

    var chunks: MutableList<Chunk> = mutableListOf()

    val players: MutableList<Player> get() = PlayerManager.players.filter { it.world != null && it.world == this }.toMutableList()

    fun sendMessage(message: String) { this.sendMessage(message.toComponent()) }
    fun sendMessage(component: Component) { players.sendMessage(component) }
    fun sendActionBar(message: String) { this.sendActionBar(message.toComponent()) }
    fun sendActionBar(component: Component) { players.sendActionBar(component) }



    init {
        // Generate initial chunks
        val size = Vector2(6, 6)
        for(xr in (size.x * -1)..size.x) {
            for(yr in (size.y * -1)..size.y) {
                val chunk = Chunk(xr, yr, this)
                chunk.sections.forEach {
                    it.blockPalette.fill(1)
                    it.biomePalette.fill(3)
                }
                chunk.cacheChunkDataPacket()
                chunks.add(chunk)
            }
        }

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