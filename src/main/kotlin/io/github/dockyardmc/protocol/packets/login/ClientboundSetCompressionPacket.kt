package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetCompressionPacket(compression: Int) : ClientboundPacket() {

    init {
        buffer.writeVarInt(compression)
    }

}