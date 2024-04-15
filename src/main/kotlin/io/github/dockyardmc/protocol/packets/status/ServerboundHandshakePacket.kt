package io.github.dockyardmc.protocol.packets.status

import io.github.dockyardmc.PacketProcessor
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.extentions.readVarInt

class ServerboundHandshakePacket(
    val version: Int,
    val serverAddress: String,
    val port: Short,
    val nextState: Int,
): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext) {
        processor.handler.handleHandshake(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundHandshakePacket {
            return ServerboundHandshakePacket(
                version = byteBuf.readVarInt(),
                serverAddress = byteBuf.readUtf(),
                port = byteBuf.readShort(),
                nextState = byteBuf.readVarInt()
            )
        }
    }
}