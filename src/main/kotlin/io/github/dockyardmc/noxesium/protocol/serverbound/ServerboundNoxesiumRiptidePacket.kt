package io.github.dockyardmc.noxesium.protocol.serverbound

import io.github.dockyardmc.protocol.plugin.messages.PluginMessage
import io.github.dockyardmc.tide.stream.StreamCodec

data class ServerboundNoxesiumRiptidePacket(
    val slot: Int
) : PluginMessage {

    override fun getStreamCodec(): StreamCodec<out PluginMessage> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.VAR_INT, ServerboundNoxesiumRiptidePacket::slot,
            ::ServerboundNoxesiumRiptidePacket
        )
    }
}