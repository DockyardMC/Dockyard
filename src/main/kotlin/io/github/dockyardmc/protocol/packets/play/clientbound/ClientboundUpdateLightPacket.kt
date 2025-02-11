package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.ChunkLight

class ClientboundUpdateLightPacket(val chunkX: Int, val chunkZ: Int, val light: ChunkLight): ClientboundPacket() {

    init {
        data.writeVarInt(chunkX)
        data.writeVarInt(chunkZ)
        light.write(data)
    }

}