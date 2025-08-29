package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player's sneaking state changes")
data class PlayerSneakToggleEvent(val player: Player, val sneaking: Boolean, override val context: Event.Context) : Event