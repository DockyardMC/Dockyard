package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundDisplayObjectivePacket(position: ObjectivePosition, text: String) : ClientboundPacket() {

    init {
        buffer.writeVarInt(position.ordinal)
        buffer.writeString(text)
    }

}

enum class ObjectivePosition {
    LIST,
    SIDEBAR,
    BELOW_NAME
}