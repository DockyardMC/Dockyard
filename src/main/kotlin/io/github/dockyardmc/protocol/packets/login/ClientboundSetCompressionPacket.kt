package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Compression")
@ClientboundPacketInfo(0x03, ProtocolState.LOGIN)
class ClientboundSetCompressionPacket(compression: Int): ClientboundPacket() {

    init {
        data.writeVarInt(compression)
    }

}