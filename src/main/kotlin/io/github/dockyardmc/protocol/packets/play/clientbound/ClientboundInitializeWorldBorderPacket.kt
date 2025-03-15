package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeVarLong
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundInitializeWorldBorderPacket(
    oldDiameter: Double,
    newDiameter: Double,
    speed: Long,
    warningBlocks: Int,
    warningTime: Int,
) : ClientboundPacket() {

    init {
        buffer.writeDouble(10.0)
        buffer.writeDouble(10.0)
        buffer.writeDouble(oldDiameter)
        buffer.writeDouble(newDiameter)

        buffer.writeVarLong(speed)

        buffer.writeVarInt(10)
        buffer.writeVarInt(warningBlocks)
        buffer.writeVarInt(warningTime)
    }
}