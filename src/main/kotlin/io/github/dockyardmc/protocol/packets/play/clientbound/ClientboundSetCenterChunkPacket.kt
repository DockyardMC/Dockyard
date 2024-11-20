package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.ChunkPos

class ClientboundSetCenterChunkPacket(chunkPos: ChunkPos): ClientboundPacket() {

    init {
        data.writeVarInt(chunkPos.x)
        data.writeVarInt(chunkPos.z)
    }

}