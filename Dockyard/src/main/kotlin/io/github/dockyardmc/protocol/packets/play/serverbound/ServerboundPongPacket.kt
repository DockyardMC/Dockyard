package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.now
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPongPacket(val id: Int) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val lastPingRequest = player.lastPingRequest ?: return
        val ping = now() - lastPingRequest
        player.ping = ping
        if(player.lastPingRequestFuture != null) player.lastPingRequestFuture!!.complete(ping)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundPongPacket {
            return ServerboundPongPacket(buffer.readInt())
        }
    }
}