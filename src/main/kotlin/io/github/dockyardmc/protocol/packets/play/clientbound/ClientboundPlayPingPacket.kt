package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPlayPingPacket(time: Int): ClientboundPacket() {

    init {
        data.writeInt(time)
    }
}