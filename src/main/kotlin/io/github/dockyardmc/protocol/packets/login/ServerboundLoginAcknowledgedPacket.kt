package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@ServerboundPacketInfo(3, ProtocolState.LOGIN)
class ServerboundLoginAcknowledgedPacket: ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.loginHandler.handleLoginAcknowledge(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundLoginAcknowledgedPacket {
            return ServerboundLoginAcknowledgedPacket()
        }
    }
}