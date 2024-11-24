package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.bitMask
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundSetPlayerOnGroundPacket(
    val isOnGround: Boolean,
    val horizontalCollision: Boolean,
) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.player.isOnGround = isOnGround
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetPlayerOnGroundPacket {
            val mask = buf.readByte()

            val isOnGround = bitMask(mask, 1)
            val isHorizontalCollision = bitMask(mask, 2)

            return ServerboundSetPlayerOnGroundPacket(isOnGround, isHorizontalCollision)
        }
    }

}