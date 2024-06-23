package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.registry.DimensionTypes

@WikiVGEntry("Respawn")
@ClientboundPacketInfo(0x47, ProtocolState.PLAY)
class ClientboundRespawnPacket(dataKept: RespawnDataKept = RespawnDataKept.NO_DATA_KEPT) : ClientboundPacket() { //nice
    init {
        data.writeVarInt(DimensionTypes.OVERWORLD.id)
        data.writeUtf("world")
        data.writeLong(0)
        data.writeByte(1)
        data.writeByte(-1)
        data.writeBoolean(false)
        data.writeBoolean(true)
        data.writeBoolean(false)
        data.writeVarInt(0)
        data.writeByte(dataKept.bitMask)
    }

    enum class RespawnDataKept(val bitMask: Int) {
        NO_DATA_KEPT(0x00),
        KEEP_ATTRIBUTES(0x01),
        KEEP_METADATA(0x02),
        KEEP_ALL(0x03)
    }
}

