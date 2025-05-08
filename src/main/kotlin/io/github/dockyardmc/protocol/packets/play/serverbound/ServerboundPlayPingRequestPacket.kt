package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayPingResponsePacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPlayPingRequestPacket(val payload: Long) : ServerboundPacket {
    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        connection.sendPacket(ClientboundPlayPingResponsePacket(payload), processor)
    }

    companion object : NetworkReadable<ServerboundPlayPingRequestPacket> {
        override fun read(buffer: ByteBuf): ServerboundPlayPingRequestPacket {
            return ServerboundPlayPingRequestPacket(
                buffer.readLong()
            )
        }
    }
}
