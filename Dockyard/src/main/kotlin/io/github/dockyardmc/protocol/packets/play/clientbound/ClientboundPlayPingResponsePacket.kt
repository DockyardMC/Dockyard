package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPlayPingResponsePacket(val number: Long): ClientboundPacket() {

    init {
        data.writeLong(number)
    }

}