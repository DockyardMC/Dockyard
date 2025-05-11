package io.github.dockyardmc.utils

import io.github.dockyardmc.player.Player

fun isDoubleInteract(player: Player): Boolean {
    return System.currentTimeMillis() - player.lastInteractionTime <= 5
}
