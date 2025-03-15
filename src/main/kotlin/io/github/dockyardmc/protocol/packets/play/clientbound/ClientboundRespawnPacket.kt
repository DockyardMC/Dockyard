package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.generators.FlatWorldGenerator

class ClientboundRespawnPacket(player: Player, dataKept: RespawnDataKept = RespawnDataKept.NO_DATA_KEPT) : ClientboundPacket() { //nice
    init {
        buffer.writeVarInt(player.world.dimensionType.getProtocolId())
        buffer.writeString(player.world.name)
        buffer.writeLong(0)
        buffer.writeByte(player.gameMode.value.ordinal)
        buffer.writeByte(-1)
        buffer.writeBoolean(false)
        buffer.writeBoolean(player.world.generator::class == FlatWorldGenerator::class)
        buffer.writeBoolean(false)
        buffer.writeVarInt(0)
        buffer.writeByte(dataKept.bitMask.toInt())
        buffer.writeVarInt(player.world.seaLevel)
    }

    enum class RespawnDataKept(val bitMask: Byte) {
        NO_DATA_KEPT(0x00),
        KEEP_ATTRIBUTES(0x01),
        KEEP_METADATA(0x02),
        KEEP_ALL(0x03)
    }
}