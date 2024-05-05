package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.extentions.writeUtf

class ClientboundStatusResponsePacket(statusJson: String): ClientboundPacket(0) {

    init {
        data.writeUtf(statusJson)
    }
}