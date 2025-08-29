package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player changes sprinting state")
data class PlayerSprintToggleEvent(val player: Player, val sprinting: Boolean, override val context: Event.Context) : Event