package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.Light

class ClientboundUpdateLightPacket(
    chunkX: Int,
    chunkZ: Int,
    light: Light
) : ClientboundPacket() {
    init {
        buffer.writeVarInt(chunkX)
        buffer.writeVarInt(chunkZ)
        light.write(buffer)
    }
}