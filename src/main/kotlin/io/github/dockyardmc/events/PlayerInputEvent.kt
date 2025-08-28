package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player uses movement input")
data class PlayerIputEvent(
    val player: Player,
    val forward: Boolean,
    val backward: Boolean,
    val left: Boolean,
    val right: Boolean,
    val jump: Boolean,
    val shift: Boolean,
    val sprint: Boolean,
    override val context: Event.Context,
): Event