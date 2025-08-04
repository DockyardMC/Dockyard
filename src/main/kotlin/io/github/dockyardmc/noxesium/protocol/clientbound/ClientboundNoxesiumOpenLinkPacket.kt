package io.github.dockyardmc.noxesium.protocol.clientbound

import io.github.dockyardmc.codec.ComponentCodecs
import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs

data class ClientboundNoxesiumOpenLinkPacket(
    val text: Component?,
    val url: String
) : NoxesiumPacket {

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = Codec.of(
            "text", ComponentCodecs.ComponentType.optional(), ClientboundNoxesiumOpenLinkPacket::text,
            "url", Codecs.String, ClientboundNoxesiumOpenLinkPacket::url,
            ::ClientboundNoxesiumOpenLinkPacket
        )
    }

}