package io.github.dockyardmc.events.noxesium

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.events.CancellableEvent
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.player.Player

@EventDocumentation("when server receives packet from noxesium")
class NoxesiumPacketReceiveEvent(val player: Player, val packet: NoxesiumPacket, override val context: Event.Context) : CancellableEvent()