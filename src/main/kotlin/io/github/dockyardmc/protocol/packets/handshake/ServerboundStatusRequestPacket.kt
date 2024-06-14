package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Status Request")
@ServerboundPacketInfo(0x00, ProtocolState.STATUS)
class ServerboundStatusRequestPacket: ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.statusHandler.handleStatusRequest(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundStatusRequestPacket {
            return ServerboundStatusRequestPacket()
        }
    }

}