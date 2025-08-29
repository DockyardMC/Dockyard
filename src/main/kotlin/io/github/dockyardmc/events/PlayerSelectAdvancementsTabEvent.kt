package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundSelectAdvancementsTabPacket

@EventDocumentation("when player selects a tab in advancement screen")
data class PlayerSelectAdvancementsTabEvent(val player: Player, val action: ServerboundSelectAdvancementsTabPacket.Action, val tabId: String?, override val context: Event.Context) : CancellableEvent()