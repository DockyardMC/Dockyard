package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundConfigurationPluginMessagePacket(val channel: String, val data: ByteBuf) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.configurationHandler.handlePluginMessage(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundConfigurationPluginMessagePacket {
            val channel = byteBuf.readString(32767)
            val data = byteBuf.readBytes(byteBuf.readableBytes())
            return ServerboundConfigurationPluginMessagePacket(channel, data)
        }
    }
}