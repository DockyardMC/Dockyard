package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player


@EventDocumentation("when player changes their held slot", true)
class PlayerSelectedHotbarSlotChangeEvent(val player: Player, val slot: Int): CancellableEvent() {
    override val context = Event.Context(players = setOf(player))
}