package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.ChunkPos

class ClientboundUnloadChunkPacket(chunkPos: ChunkPos): ClientboundPacket() {

    init {
        data.writeInt(chunkPos.z)
        data.writeInt(chunkPos.x)
    }

}