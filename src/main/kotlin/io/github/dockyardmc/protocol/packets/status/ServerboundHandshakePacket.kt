package io.github.dockyardmc.protocol.packets.status

import LoggerSettings
import io.github.dockyardmc.PacketProcessor
import io.github.dockyardmc.extentions.readEnum
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.extentions.readVarInt
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder.EncoderMode

class ServerboundHandshakePacket(
    val version: Int,
    val serverAddress: String,
    val port: Short,
    val nextState: Int,
): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext) {
        processor.statusHandler.handleHandshake(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundHandshakePacket {
            return ServerboundHandshakePacket(
                version = byteBuf.readVarInt(),
                serverAddress = byteBuf.readUtf(255),
                port = byteBuf.readUnsignedShort().toShort(),
                nextState = byteBuf.readByte().toInt()
            )
        }
    }
}

public enum class TEST(var i: Int) {
    ABC(1),
    CDE(2)
}