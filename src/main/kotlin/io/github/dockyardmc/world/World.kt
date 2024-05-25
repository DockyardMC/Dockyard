package io.github.dockyardmc.world

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent

class World(var name: String = "world", var dimensionType: DimensionType = DimensionType.OVERWORLD, worldSeed: String = "trans rights!!") {

    var seed = worldSeed.SHA256Long()
    var seedBytes = worldSeed.SHA256String()
    var worldBorder = WorldBorder(this)

    var chunks: MutableList<Chunk> = mutableListOf()

    val players: MutableList<Player> get() = PlayerManager.players.filter { it.world != null && it.world == this }.toMutableList()

    fun sendMessage(message: String) { this.sendMessage(message.toComponent()) }
    fun sendMessage(component: Component) { players.sendMessage(component) }
    fun sendActionBar(message: String) { this.sendActionBar(message.toComponent()) }
    fun sendActionBar(component: Component) { players.sendActionBar(component) }
}

enum class DimensionType(val maxY: Int, val minY: Int) {
    OVERWORLD(320, -64),
    NETHER(255, 0),
    THE_END(255, 0)
}