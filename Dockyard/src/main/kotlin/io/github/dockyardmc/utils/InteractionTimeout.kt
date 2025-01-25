package io.github.dockyardmc.utils

import io.github.dockyardmc.player.Player

fun isDoubleInteract(player: Player): Boolean {

    val now = System.currentTimeMillis()
    if(player.lastInteractionTime == 0L) {
        player.lastInteractionTime = now
        return false
    }

    val sub = (now - player.lastInteractionTime)
    if (sub <= 5 || now == player.lastInteractionTime) {
        return true
    }
    player.lastInteractionTime = now
    return false

}