package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundKeepAlivePacket(keepAliveId: Long) : ClientboundPacket() {

    init {
        data.writeLong(keepAliveId)
    }

}