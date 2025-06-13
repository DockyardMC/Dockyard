package io.github.dockyardmc.extentions

import io.github.dockyardmc.world.World


fun World.sendMessage(message: String, isSystem: Boolean = false) {
    players.sendMessage(message, isSystem)
}

fun World.sendActionBar(message: String) {
    players.sendActionBar(message)
}