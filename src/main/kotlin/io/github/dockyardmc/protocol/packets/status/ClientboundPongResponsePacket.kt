package io.github.dockyardmc.protocol.packets.status

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPongResponsePacket(val time: Long): ClientboundPacket() {

    init {
        data.writeByte(0x09)
        data.writeVarInt(1)
        data.writeLong(time)
    }
}