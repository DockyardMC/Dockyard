package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundStatusResponsePacket(statusJson: String): ClientboundPacket() {

    init {
        buffer.writeString(statusJson)
    }
}