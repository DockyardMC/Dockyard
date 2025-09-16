package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.world.LightData

data class ClientboundUpdateLightPacket(
    val chunkX: Int,
    val chunkZ: Int,
    val light: LightData
) : ClientboundPacket() {

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.VAR_INT, ClientboundUpdateLightPacket::chunkX,
            StreamCodec.VAR_INT, ClientboundUpdateLightPacket::chunkZ,
            LightData.STREAM_CODEC, ClientboundUpdateLightPacket::light,
            ::ClientboundUpdateLightPacket
        )
    }

    init {
        STREAM_CODEC.write(buffer, this)
    }
}