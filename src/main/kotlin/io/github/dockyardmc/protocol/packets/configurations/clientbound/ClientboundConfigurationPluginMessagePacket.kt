package io.github.dockyardmc.protocol.packets.configurations.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.plugin.messages.PluginMessage
import io.github.dockyardmc.tide.stream.StreamCodec

data class ClientboundConfigurationPluginMessagePacket(val contents: PluginMessage.Contents) : ClientboundPacket() {

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            PluginMessage.Contents.STREAM_CODEC, ClientboundConfigurationPluginMessagePacket::contents,
            ::ClientboundConfigurationPluginMessagePacket
        )
    }

    init {
        STREAM_CODEC.write(buffer, this)
    }
}