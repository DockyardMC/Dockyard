package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundUpdateScorePacket(objective: String, line: Int, text: String) : ClientboundPacket() {
    init {
        data.writeString("line-$line")
        data.writeString(objective)
        data.writeVarInt(line)
        data.writeBoolean(true)
        data.writeTextComponent(text)
        data.writeBoolean(true)
        data.writeVarInt(0)
    }
}