package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundWorldTimePacket(val worldAge: Long, val time: Long): ClientboundPacket(0x62) {

    init {
        data.writeLong(worldAge)
        data.writeLong(time)
    }
}