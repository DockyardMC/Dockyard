package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Clientbound Keep Alive (play)")
@ClientboundPacketInfo(0x26, ProtocolState.PLAY)
class ClientboundKeepAlivePacket(keepAliveId: Long): ClientboundPacket() {

    init {
        data.writeLong(keepAliveId)
    }

}