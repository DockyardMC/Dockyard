package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Set Player Position and Rotation")
@ServerboundPacketInfo(27, ProtocolState.PLAY)
class ServerboundSetPlayerPositionAndRotationPacket(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    val isOnGround: Boolean
): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.playHandler.handlePlayerPositionAndRotationUpdates(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetPlayerPositionAndRotationPacket {
            return ServerboundSetPlayerPositionAndRotationPacket(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean()
            )
        }
    }
}