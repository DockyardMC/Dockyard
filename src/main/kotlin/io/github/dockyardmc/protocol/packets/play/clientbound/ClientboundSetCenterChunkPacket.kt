package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetCenterChunkPacket(chunkX: Int, chunkZ: Int): ClientboundPacket(0x52) {

    init {
        data.writeVarInt(chunkX)
        data.writeVarInt(chunkZ)
    }

}