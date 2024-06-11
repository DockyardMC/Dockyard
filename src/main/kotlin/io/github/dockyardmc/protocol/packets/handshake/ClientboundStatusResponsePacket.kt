package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundStatusResponsePacket(statusJson: String): ClientboundPacket(0, ProtocolState.HANDSHAKE) {

    init {
        data.writeUtf(statusJson)
    }
}