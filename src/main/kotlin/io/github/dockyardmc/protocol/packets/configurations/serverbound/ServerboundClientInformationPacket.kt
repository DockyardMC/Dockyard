package io.github.dockyardmc.protocol.packets.configurations.serverbound

import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.types.ClientSettings
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

data class ServerboundClientInformationPacket(
    val clientSettings: ClientSettings
) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.configurationHandler.handleClientInformation(this, connection)
    }

    companion object {

        val STREAM_CODEC = StreamCodec.of(
            ClientSettings.STREAM_CODEC, ServerboundClientInformationPacket::clientSettings,
            ::ServerboundClientInformationPacket
        )

        fun read(buffer: ByteBuf): ServerboundClientInformationPacket {
            return STREAM_CODEC.read(buffer)
        }
    }
}