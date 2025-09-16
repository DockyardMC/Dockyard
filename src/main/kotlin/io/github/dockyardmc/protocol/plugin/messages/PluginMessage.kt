package io.github.dockyardmc.protocol.plugin.messages

import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

interface PluginMessage {
    fun getStreamCodec(): StreamCodec<out PluginMessage>

    data class Contents(val channel: String, val data: ByteBuf) {
        companion object {
            val STREAM_CODEC = StreamCodec.of(
                StreamCodec.STRING, Contents::channel,
                StreamCodec.RAW_BYTES, Contents::data,
                ::Contents
            )
        }
    }
}