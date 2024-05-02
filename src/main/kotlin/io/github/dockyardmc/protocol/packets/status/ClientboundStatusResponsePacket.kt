package io.github.dockyardmc.protocol.packets.status

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt

class ClientboundStatusResponsePacket(statusJson: String): ClientboundPacket(0) {

    init {
        data.writeUtf(statusJson)
    }
}