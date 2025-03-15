package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundResetScorePacket(name: String, objective: String) : ClientboundPacket() {

    init {
        buffer.writeString(name)
        buffer.writeBoolean(true)
        buffer.writeString(objective)
    }

}