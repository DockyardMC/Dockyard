package io.github.dockyardmc.protocol.plugin.messages

import io.github.dockyardmc.tide.stream.StreamCodec

data class UnregisterPluginMessage(val channels: List<String>) : PluginMessage {
    
    companion object {
        val STREAM_CODEC = StreamCodec.of(RegisterPluginMessage.MOJANG_FUCKED_STRING_LIST_STREAM_CODEC, UnregisterPluginMessage::channels, ::UnregisterPluginMessage)
    }

    override fun getStreamCodec(): StreamCodec<out PluginMessage> {
        return STREAM_CODEC
    }
}
