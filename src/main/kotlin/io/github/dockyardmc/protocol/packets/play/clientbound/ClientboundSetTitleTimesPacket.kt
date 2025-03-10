package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetTitleTimesPacket(
    val fadeIn: Int,
    val stay: Int,
    val fadeOut: Int
) : ClientboundPacket() {
    init {
        data.writeInt(fadeIn)
        data.writeInt(stay)
        data.writeInt(fadeOut)
    }
}