package io.github.dockyardmc.extentions

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scroll.extensions.stripComponentTags
import io.github.dockyardmc.utils.Console

fun DockyardServer.Companion.broadcastMessage(message: String, isSystem: Boolean = false) {
    PlayerManager.players.sendMessage(message); Console.sendMessage(message.stripComponentTags())
}

fun DockyardServer.Companion.broadcastActionBar(message: String) {
    PlayerManager.players.sendActionBar(message)
}

fun DockyardServer.Companion.sendTitle(title: String, subtitle: String = "", fadeIn: Int = 10, stay: Int = 60, fadeOut: Int = 10) {
    PlayerManager.players.sendTitle(title, subtitle, fadeIn, stay, fadeOut)
}

fun broadcastMessage(message: String, isSystem: Boolean = false) {
    DockyardServer.broadcastMessage(message, isSystem)
}
