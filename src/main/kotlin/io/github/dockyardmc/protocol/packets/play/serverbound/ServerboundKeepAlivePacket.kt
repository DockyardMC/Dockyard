package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Serverbound Keep Alive (play)")
@ServerboundPacketInfo(21, ProtocolState.PLAY)
class ServerboundKeepAlivePacket(val keepAliveId: Long): ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.playHandler.handleKeepAlive(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundKeepAlivePacket {
            return ServerboundKeepAlivePacket(buf.readLong())
        }
    }

}