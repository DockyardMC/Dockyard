package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand

@EventDocumentation("when player swings their hand", false)
class PlayerSwingHandEvent(val player: Player, val hand: PlayerHand): Event {
    override val context = elements(player)
}