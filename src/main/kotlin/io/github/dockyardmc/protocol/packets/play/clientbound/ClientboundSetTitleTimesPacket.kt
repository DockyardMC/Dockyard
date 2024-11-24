package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Title Animation Times")
@ClientboundPacketInfo(0x66, ProtocolState.PLAY)
class ClientboundSetTitleTimesPacket(
    val fadeIn: Int,
    val stay: Int,
    val fadeOut: Int
): ClientboundPacket() {
    init {
        data.writeInt(fadeIn)
        data.writeInt(stay)
        data.writeInt(fadeOut)
    }
}