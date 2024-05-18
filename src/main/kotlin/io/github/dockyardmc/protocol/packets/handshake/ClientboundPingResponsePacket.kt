package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPingResponsePacket(time: Long): ClientboundPacket(1) {

    init {
        data.writeLong(time)
    }
}