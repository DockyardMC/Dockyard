package io.github.dockyardmc.events.noxesium

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.noxesium.protocol.serverbound.ServerboundNoxesiumQibTriggeredPacket
import io.github.dockyardmc.player.Player

@EventDocumentation("when server receives qib trigger packet from noxesium")
data class NoxesiumQibTriggeredEvent(val player: Player, val behaviour: String, val qibType: ServerboundNoxesiumQibTriggeredPacket.Type, val entityId: Int, override val context: Event.Context) : Event