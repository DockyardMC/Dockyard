package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetExperiencePacket(bar: Float, level: Int) : ClientboundPacket() {

    init {
        data.writeFloat(bar)
        data.writeVarInt(level)
        data.writeVarInt(0)
    }

}