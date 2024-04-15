package io.github.dockyardmc.protocol.packets.status

import io.github.dockyardmc.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPingRequestPacket(val time: Long): ServerboundPacket {

    companion object {
        fun read(buf: ByteBuf): ServerboundPingRequestPacket {
            return ServerboundPingRequestPacket(buf.readLong())
        }
    }

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext) {
        processor.handler.handlePing(this, connection)
    }

}