package io.github.dockyardmc.protocol.packets.status

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPingResponsePacket(time: Long): ClientboundPacket(1) {

    init {
        data.writeVarInt(1)
        data.writeLong(time)
    }
}