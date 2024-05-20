package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import log

class ServerboundPluginMessagePacket(var channel: String, var data: String): ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.configurationHandler.handlePluginMessage(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf, size: Int): ServerboundPluginMessagePacket {
            val channel = buf.readUtf()
            val leftBits = buf.readableBytes()
            log("Leftover bytes after channel was read: $leftBits or | (${size - buf.readerIndex()})")
            val restOfData = buf.readBytes(leftBits)

            return ServerboundPluginMessagePacket(channel, "")
        }
    }
}