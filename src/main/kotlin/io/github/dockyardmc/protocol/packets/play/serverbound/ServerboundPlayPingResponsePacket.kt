package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.debug
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPlayPingResponsePacket(val number: Long): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
//        processor.player.ping = number
//        processor.player.sendPacket(ClientboundPlayPingResponsePacket(number))
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundPlayPingResponsePacket {
            return ServerboundPlayPingResponsePacket(buffer.readLong())
        }
    }
}