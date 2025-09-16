package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.protocol.plugin.messages.PluginMessage
import io.github.dockyardmc.tide.stream.StreamCodec

data class ClientboundNoxesiumCustomSoundModifyPacket(
    val id: Int,
    val volume: Float,
    val interpolationTicks: Int,
    val startVolume: Float? = null
) : PluginMessage {

    override fun getStreamCodec(): StreamCodec<out PluginMessage> {
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