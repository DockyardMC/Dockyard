package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundLoginCompressionPacket: ClientboundPacket(3) {

    init {
        data.writeVarInt(-1)
    }

}