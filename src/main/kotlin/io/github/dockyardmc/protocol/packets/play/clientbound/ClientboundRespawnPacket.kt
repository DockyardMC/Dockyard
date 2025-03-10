package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.generators.FlatWorldGenerator

class ClientboundRespawnPacket(player: Player, dataKept: RespawnDataKept = RespawnDataKept.NO_DATA_KEPT) : ClientboundPacket() { //nice
    init {
        data.writeVarInt(player.world.dimensionType.getProtocolId())
        data.writeString(player.world.name)
        data.writeLong(0)
        data.writeByte(player.gameMode.value.ordinal)
        data.writeByte(-1)
        data.writeBoolean(false)
        data.writeBoolean(player.world.generator::class == FlatWorldGenerator::class)
        data.writeBoolean(false)
        data.writeVarInt(0)
        data.writeByte(dataKept.bitMask.toInt())
        data.writeVarInt(player.world.seaLevel)
    }

    enum class RespawnDataKept(val bitMask: Byte) {
        NO_DATA_KEPT(0x00),
        KEEP_ATTRIBUTES(0x01),
        KEEP_METADATA(0x02),
        KEEP_ALL(0x03)
    }
}