package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.chunk.ChunkPos

class ClientboundUnloadChunkPacket(chunkPos: ChunkPos): ClientboundPacket() {

    init {
        buffer.writeInt(chunkPos.z)
        buffer.writeInt(chunkPos.x)
    }

}