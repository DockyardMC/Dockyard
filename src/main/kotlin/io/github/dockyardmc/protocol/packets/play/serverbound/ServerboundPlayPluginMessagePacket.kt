package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.extentions.toByteArraySafe
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import log

class ServerboundPlayPluginMessagePacket(val channel: String, val data: ByteArray): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.playHandler.handlePluginMessage(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf, size: Int): ServerboundPlayPluginMessagePacket {
            val channel = byteBuf.readUtf()
            val data = byteBuf.readBytes(byteBuf.readableBytes())
            return ServerboundPlayPluginMessagePacket(channel, data.toByteArraySafe())
        }
    }

}