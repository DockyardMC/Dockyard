package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Clientbound Keep Alive (play)")
class ClientboundKeepAlivePacket(keepAliveId: Long): ClientboundPacket(0x26, ProtocolState.PLAY) {

    init {
        data.writeLong(keepAliveId)
    }

}