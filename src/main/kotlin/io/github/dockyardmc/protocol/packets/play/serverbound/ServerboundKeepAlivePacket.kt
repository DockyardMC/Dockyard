package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundKeepAlivePacket(val keepAliveId: Long) : ServerboundPacket {
    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.playHandler.handleKeepAlive(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundKeepAlivePacket {
            return ServerboundKeepAlivePacket(buf.readLong())
        }
    }

}