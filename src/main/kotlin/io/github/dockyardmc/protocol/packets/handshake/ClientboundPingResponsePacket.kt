package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Ping Response (status)")
@ClientboundPacketInfo(0x01, ProtocolState.HANDSHAKE)
class ClientboundPingResponsePacket(time: Long): ClientboundPacket() {

    init {
        data.writeLong(time)
    }
}