package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundWorldTimePacket(val worldAge: Long, val time: Long): ClientboundPacket(0x62, ProtocolState.PLAY) {

    init {
        data.writeLong(worldAge)
        data.writeLong(time)
    }
}