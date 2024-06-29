package io.github.dockyardmc.extentions

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.Console

fun DockyardServer.Companion.broadcastMessage(message: String) { this.broadcastMessage(message.toComponent()) }
fun DockyardServer.Companion.broadcastMessage(component: Component) { PlayerManager.players.sendMessage(component); Console.sendMessage(component.stripStyling()) }
fun DockyardServer.Companion.broadcastActionBar(message: String) { this.broadcastActionBar(message.toComponent()) }
fun DockyardServer.Companion.broadcastActionBar(component: Component) { PlayerManager.players.sendActionBar(component) }
fun DockyardServer.Companion.sendTitle(title: String, subtitle: String = "", fadeIn: Int = 10, stay: Int = 60, fadeOut: Int = 10) {
    PlayerManager.players.sendTitle(title, subtitle, fadeIn, stay, fadeOut)
}