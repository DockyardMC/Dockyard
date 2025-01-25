package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.bitMask
import io.github.dockyardmc.utils.vectors.Vector3d
import io.github.dockyardmc.utils.vectors.readVector3d
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Set Player Position")
@ServerboundPacketInfo(26, ProtocolState.PLAY)
class ServerboundSetPlayerPositionPacket(
    val vector3d: Vector3d,
    val isOnGround: Boolean,
    val horizontalCollision: Boolean,
): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.playHandler.handlePlayerPositionAndRotationUpdates(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetPlayerPositionPacket {
            val vector3d = buf.readVector3d()
            val mask = buf.readByte()

            val isOnGround = bitMask(mask, 1)
            val isHorizontalCollision = bitMask(mask, 2)

            return ServerboundSetPlayerPositionPacket(vector3d, isOnGround, isHorizontalCollision)
        }
    }
}