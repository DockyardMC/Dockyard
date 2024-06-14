package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Status Response")
class ClientboundStatusResponsePacket(statusJson: String): ClientboundPacket(0x00, ProtocolState.HANDSHAKE) {

    init {
        data.writeUtf(statusJson)
    }
}