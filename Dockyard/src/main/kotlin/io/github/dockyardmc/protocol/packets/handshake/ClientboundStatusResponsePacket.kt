package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.extentions.writeString

class ClientboundStatusResponsePacket(statusJson: String): ClientboundPacket() {

    init {
        data.writeString(statusJson)
    }
}