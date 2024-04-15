package io.github.dockyardmc.protocol.packets.status

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt

class ClientboundStatusResponsePacket(length: Int, id: Int, statusJson: String): ClientboundPacket() {

    init {
        data.writeVarInt(length)
        data.writeVarInt(id)
        data.writeUtf(statusJson)
    }
}