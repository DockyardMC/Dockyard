package io.github.dockyardmc.events

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundSelectAdvancementsTabPacket

class PlayerSelectAdvancementsTabEvent(val player: Player, val action: ServerboundSelectAdvancementsTabPacket.Action, val tabId: String?) : CancellableEvent() {
    override val context: Event.Context = Event.Context(players = setOf(player))
}