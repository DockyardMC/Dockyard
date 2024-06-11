package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundPingResponsePacket(time: Long): ClientboundPacket(1, ProtocolState.HANDSHAKE) {


    init {
        data.writeLong(time)
    }
}