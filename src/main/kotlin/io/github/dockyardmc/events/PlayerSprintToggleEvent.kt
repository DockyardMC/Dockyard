package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player changes sprinting state", false)
class PlayerSprintToggleEvent(val player: Player, val sprinting: Boolean): Event