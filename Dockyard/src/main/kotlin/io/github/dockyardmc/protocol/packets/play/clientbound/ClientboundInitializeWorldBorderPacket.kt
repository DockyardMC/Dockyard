package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeVarLong
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Initialize World Border")
@ClientboundPacketInfo(0x25, ProtocolState.PLAY)
class ClientboundInitializeWorldBorderPacket(
    oldDiameter: Double,
    newDiameter: Double,
    speed: Long,
    warningBlocks: Int,
    warningTime: Int,
): ClientboundPacket() {

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