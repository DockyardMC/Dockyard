package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundSetPlayerPositionPacket(
    val x: Double,
    val y: Double,
    val z: Double,
    val isOnGround: Boolean
): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.playHandler.handlePlayerPositionAndRotationUpdates(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundSetPlayerPositionPacket {
            return ServerboundSetPlayerPositionPacket(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readBoolean()
            )
        }
    }
}