package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Display Objective")
@ClientboundPacketInfo(0x57, ProtocolState.PLAY)
class ClientboundDisplayObjectivePacket(position: ObjectivePosition, text: String): ClientboundPacket() {

    init {
        data.writeVarInt(position.ordinal)
        data.writeUtf(text)
    }

}

enum class ObjectivePosition {
    LIST,
    SIDEBAR,
    BELOW_NAME
}