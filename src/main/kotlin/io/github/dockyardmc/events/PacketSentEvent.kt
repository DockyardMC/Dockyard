package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.protocol.packets.ClientboundPacket

@EventDocumentation("server sends packet to client", true)
class PacketSentEvent(packet: ClientboundPacket): Event