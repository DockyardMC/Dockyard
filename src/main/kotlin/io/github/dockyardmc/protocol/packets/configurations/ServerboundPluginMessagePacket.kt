package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPluginMessagePacket(var channel: String, var data: String): ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.configurationHandler.handlePluginMessage(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPluginMessagePacket {
            return ServerboundPluginMessagePacket(buf.readUtf(), buf.readUtf())
        }
    }

}