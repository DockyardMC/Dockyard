package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundUpdateScorePacket(objective: String, line: Int, text: String) : ClientboundPacket() {
    init {
        buffer.writeString("line-$line")
        buffer.writeString(objective)
        buffer.writeVarInt(line)
        buffer.writeBoolean(true)
        buffer.writeTextComponent(text)
        buffer.writeBoolean(true)
        buffer.writeVarInt(0)
    }
}