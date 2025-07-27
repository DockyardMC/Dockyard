package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.types.ClientSettings
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundClientInformationPacket(
    val clientSettings: ClientSettings
) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.configurationHandler.handleClientInformation(this, connection)
    }

    companion object {

        val STREAM_CODEC = Codec.of(
            "client_settings", ClientSettings.STREAM_CODEC, ServerboundClientInformationPacket::clientSettings,
            ::ServerboundClientInformationPacket
        )

        fun read(buffer: ByteBuf): ServerboundClientInformationPacket {
            return STREAM_CODEC.readNetwork(buffer)
        }
    }
}