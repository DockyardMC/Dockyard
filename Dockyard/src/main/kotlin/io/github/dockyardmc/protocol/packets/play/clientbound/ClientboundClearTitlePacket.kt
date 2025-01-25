package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Clear Titles")
@ClientboundPacketInfo(0x0F, ProtocolState.PLAY)
class ClientboundClearTitlePacket(val reset: Boolean): ClientboundPacket() {
    init {
        data.writeBoolean(reset)
    }
}