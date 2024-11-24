package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Status Response")
@ClientboundPacketInfo(0x00, ProtocolState.HANDSHAKE)
class ClientboundStatusResponsePacket(statusJson: String): ClientboundPacket() {

    init {
        data.writeString(statusJson)
    }
}