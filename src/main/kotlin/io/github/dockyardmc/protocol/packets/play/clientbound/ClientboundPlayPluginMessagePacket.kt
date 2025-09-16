package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.plugin.messages.PluginMessage
import io.github.dockyardmc.tide.stream.StreamCodec

data class ClientboundPlayPluginMessagePacket(val contents: PluginMessage.Contents) : ClientboundPacket() {

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            PluginMessage.Contents.STREAM_CODEC, ClientboundPlayPluginMessagePacket::contents,
            ::ClientboundPlayPluginMessagePacket
        )
    }

    init {
        STREAM_CODEC.write(buffer, this)
    }
}