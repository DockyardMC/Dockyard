package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Confirm Teleportation")
@ServerboundPacketInfo(0, ProtocolState.PLAY)
class ServerboundTeleportConfirmationPacket(teleportId: Int): ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.playHandler.handleTeleportConfirmation(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundTeleportConfirmationPacket =
            ServerboundTeleportConfirmationPacket(buf.readVarInt())
    }
}