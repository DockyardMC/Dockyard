package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPingResponsePacket(time: Long): ClientboundPacket() {

    init {
        data.writeLong(time)
    }
}