package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.codec.ComponentCodecs
import io.github.dockyardmc.protocol.plugin.messages.PluginMessage
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.tide.stream.StreamCodec

data class ClientboundNoxesiumOpenLinkPacket(
    val text: Component?,
    val url: String
) : PluginMessage {

    override fun getStreamCodec(): StreamCodec<out PluginMessage> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            ComponentCodecs.STREAM.optional(), ClientboundNoxesiumOpenLinkPacket::text,
            StreamCodec.STRING, ClientboundNoxesiumOpenLinkPacket::url,
            ::ClientboundNoxesiumOpenLinkPacket
        )
    }

}