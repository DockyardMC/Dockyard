package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.tide.stream.StreamCodec

data class ClientboundNoxesiumCustomSoundModifyPacket(
    val id: Int,
    val volume: Float,
    val interpolationTicks: Int,
    val startVolume: Float? = null
) : NoxesiumPacket {

    override fun getStreamCodec(): StreamCodec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.VAR_INT, ClientboundNoxesiumCustomSoundModifyPacket::id,
            StreamCodec.FLOAT, ClientboundNoxesiumCustomSoundModifyPacket::volume,
            StreamCodec.VAR_INT, ClientboundNoxesiumCustomSoundModifyPacket::interpolationTicks,
            StreamCodec.FLOAT.optional(), ClientboundNoxesiumCustomSoundModifyPacket::startVolume,
            ::ClientboundNoxesiumCustomSoundModifyPacket
        )
    }

}