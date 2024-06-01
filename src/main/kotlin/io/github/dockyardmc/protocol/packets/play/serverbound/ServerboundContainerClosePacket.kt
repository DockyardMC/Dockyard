package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

// 0x0E - 14
class ServerboundContainerClosePacket(val windowId: Int): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        DockyardServer.broadcastMessage("<red>${processor.player} closed inventory id <yellow>$windowId")
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundContainerClosePacket {
            return ServerboundContainerClosePacket(buf.readByte().toInt())
        }
    }
}