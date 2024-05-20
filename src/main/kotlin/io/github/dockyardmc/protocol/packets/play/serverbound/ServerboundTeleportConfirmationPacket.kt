package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundTeleportConfirmationPacket(teleportId: Int): ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.playHandler.handleTeleportConfirmation(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundTeleportConfirmationPacket {
            return ServerboundTeleportConfirmationPacket(buf.readVarInt())
        }
    }
}