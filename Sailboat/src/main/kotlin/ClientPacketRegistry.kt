package io.github.dockyardmc.sailboat

import io.github.dockyardmc.protocol.packets.status.clientbound.ClientboundStatusResponsePacket
import io.github.dockyardmc.socket.PacketRegistry

object ClientPacketRegistry: PacketRegistry() {

    override fun load() {
        addStatus(ClientboundStatusResponsePacket::class, null)
    }
}