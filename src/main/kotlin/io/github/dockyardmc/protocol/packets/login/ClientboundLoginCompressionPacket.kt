package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundLoginCompressionPacket: ClientboundPacket(3, ProtocolState.LOGIN) {

    init {
        data.writeVarInt(-1)
    }

}