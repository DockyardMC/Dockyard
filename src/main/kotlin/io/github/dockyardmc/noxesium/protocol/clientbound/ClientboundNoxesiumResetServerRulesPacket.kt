package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.protocol.plugin.messages.PluginMessage
import io.github.dockyardmc.tide.stream.StreamCodec

data class ClientboundNoxesiumResetServerRulesPacket(val indices: List<Int>) : PluginMessage {

    override fun getStreamCodec(): StreamCodec<out PluginMessage> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.VAR_INT.list(), ClientboundNoxesiumResetServerRulesPacket::indices,
            ::ClientboundNoxesiumResetServerRulesPacket
        )
    }
}