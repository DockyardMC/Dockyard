package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player's horse changes jump state", false)
class HorseJumpEvent(val player: Player, isJumping: Boolean): Event