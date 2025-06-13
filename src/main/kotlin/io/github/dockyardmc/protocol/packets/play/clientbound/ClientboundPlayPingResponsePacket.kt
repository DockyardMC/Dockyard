package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPlayPingResponsePacket(payload: Long): ClientboundPacket() {

    init {
        buffer.writeLong(payload)
    }

}