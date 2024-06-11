package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundKeepAlivePacket(keepAliveId: Long): ClientboundPacket(36, ProtocolState.PLAY) {

    init {
        data.writeLong(keepAliveId)
    }

}