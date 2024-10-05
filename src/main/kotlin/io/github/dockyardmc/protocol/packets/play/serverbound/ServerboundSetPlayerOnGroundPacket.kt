package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Set Player On Ground")
@ServerboundPacketInfo(0x1D, ProtocolState.PLAY)
class ServerboundSetPlayerOnGroundPacket(var onGround: Boolean): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.player.isOnGround = onGround
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetPlayerOnGroundPacket = ServerboundSetPlayerOnGroundPacket(buf.readBoolean())
    }

}