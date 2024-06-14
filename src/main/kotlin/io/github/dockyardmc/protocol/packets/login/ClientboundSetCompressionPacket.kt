package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Compression")
class ClientboundSetCompressionPacket(compression: Int): ClientboundPacket(0x03, ProtocolState.LOGIN) {

    init {
        data.writeVarInt(compression)
    }

}