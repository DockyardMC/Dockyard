package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetHealthPacket(var health: Float, var food: Int, var saturation: Float) : ClientboundPacket() {

    init {
        data.writeFloat(health)
        data.writeVarInt(food)
        data.writeFloat(saturation)
    }
}