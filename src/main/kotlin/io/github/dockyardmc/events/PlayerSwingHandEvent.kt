package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand

@EventDocumentation("when player swings their hand")
data class PlayerSwingHandEvent(val player: Player, val hand: PlayerHand, override val context: Event.Context) : Event