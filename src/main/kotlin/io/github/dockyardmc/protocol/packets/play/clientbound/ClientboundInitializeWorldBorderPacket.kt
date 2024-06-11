package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeVarLong
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundInitializeWorldBorderPacket(oldDiameter: Double, newDiameter: Double, speed: Long, warningBlocks: Int, warningTime: Int): ClientboundPacket(0x23, ProtocolState.PLAY) {

    init {
        data.writeDouble(10.0)
        data.writeDouble(10.0)
        data.writeDouble(oldDiameter)
        data.writeDouble(newDiameter)

        data.writeVarLong(speed)

        data.writeVarInt(10)
        data.writeVarInt(warningBlocks)
        data.writeVarInt(warningTime)
    }
}