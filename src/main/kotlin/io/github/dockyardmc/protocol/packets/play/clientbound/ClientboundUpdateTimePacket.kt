package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Update Time")
@ClientboundPacketInfo(0x64, ProtocolState.PLAY)
class ClientboundUpdateTimePacket(
    val worldAge: Long,
    val time: Long,
    val isFrozen: Boolean
): ClientboundPacket() {

    init {
        data.writeLong(worldAge)
        data.writeLong(time)
        data.writeBoolean(isFrozen)
    }
}