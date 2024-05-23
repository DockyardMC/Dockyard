package io.github.dockyardmc.world

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent

class World(var name: String = "world", worldSeed: String = "trans rights!!") {

    var seed = worldSeed.SHA256Long()
    var seedBytes = worldSeed.SHA256String()
    var worldBorder = WorldBorder(this)
    val players: MutableList<Player> get() = PlayerManager.players.filter { it.world != null && it.world == this }.toMutableList()

    fun sendMessage(message: String) { this.sendMessage(message.toComponent()) }
    fun sendMessage(component: Component) { players.sendMessage(component) }
    fun sendActionBar(message: String) { this.sendActionBar(message.toComponent()) }
    fun sendActionBar(component: Component) { players.sendActionBar(component) }
}