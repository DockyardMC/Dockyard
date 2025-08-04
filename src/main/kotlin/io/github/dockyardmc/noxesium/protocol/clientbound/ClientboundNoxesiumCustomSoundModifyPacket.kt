package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs

data class ClientboundNoxesiumCustomSoundModifyPacket(
    val id: Int,
    val volume: Float,
    val interpolationTicks: Int,
    val startVolume: Float? = null
) : NoxesiumPacket {

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = Codec.of(
            "id", Codecs.VarInt, ClientboundNoxesiumCustomSoundModifyPacket::id,
            "volume", Codecs.Float, ClientboundNoxesiumCustomSoundModifyPacket::volume,
            "interpolation_ticks", Codecs.VarInt, ClientboundNoxesiumCustomSoundModifyPacket::interpolationTicks,
            "start_volume", Codecs.Float.optional(), ClientboundNoxesiumCustomSoundModifyPacket::startVolume,
            ::ClientboundNoxesiumCustomSoundModifyPacket
        )
    }

}