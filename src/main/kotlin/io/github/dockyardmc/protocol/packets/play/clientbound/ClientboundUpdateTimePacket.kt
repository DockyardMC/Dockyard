package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Update Time")
class ClientboundUpdateTimePacket(
    val worldAge: Long,
    val time: Long,
): ClientboundPacket(0x64, ProtocolState.PLAY) {

    init {
        data.writeLong(worldAge)
        data.writeLong(time)
    }
}