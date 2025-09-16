package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.plugin.PluginMessageRegistry
import io.github.dockyardmc.protocol.plugin.messages.PluginMessage
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundConfigurationPluginMessagePacket(val contents: PluginMessage.Contents) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        PluginMessageRegistry.handle<PluginMessage>(contents, processor)
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            PluginMessage.Contents.STREAM_CODEC, ServerboundConfigurationPluginMessagePacket::contents,
            ::ServerboundConfigurationPluginMessagePacket
        )

        fun read(buffer: ByteBuf): ServerboundConfigurationPluginMessagePacket {
            return STREAM_CODEC.read(buffer)
        }
    }
}