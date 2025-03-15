package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.chunk.ChunkPos

class ClientboundSetCenterChunkPacket(chunkPos: ChunkPos): ClientboundPacket() {

    init {
        buffer.writeVarInt(chunkPos.x)
        buffer.writeVarInt(chunkPos.z)
    }

}