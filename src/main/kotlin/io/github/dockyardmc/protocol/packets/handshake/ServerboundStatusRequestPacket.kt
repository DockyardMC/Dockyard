package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundStatusRequestPacket: ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.statusHandler.handleStatusRequest(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundStatusRequestPacket {
            return ServerboundStatusRequestPacket()
        }
    }

}