package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player's horse starts or stops jumping", false)
class HorseJumpEvent(val player: Player, val isJumping: Boolean): Event