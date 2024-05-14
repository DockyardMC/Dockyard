package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundKeepAlivePacket(keepAliveId: Long): ClientboundPacket(36) {

    init {
        data.writeLong(keepAliveId)
    }

}