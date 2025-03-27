package io.github.dockyardmc.events

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.serverbound.SelectAdvancementsTabAction

class PlayerSelectAdvancementsTabEvent(val player: Player, val action: SelectAdvancementsTabAction, val tabId: String?) : CancellableEvent() {
    override val context: Event.Context = Event.Context(players = setOf(player))
}