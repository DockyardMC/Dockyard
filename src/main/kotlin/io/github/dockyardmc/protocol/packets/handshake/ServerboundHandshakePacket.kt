package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket

@ServerboundPacketInfo(0, ProtocolState.HANDSHAKE)
class ServerboundHandshakePacket(
    val version: Int,
    val serverAddress: String,
    val port: Short,
    val nextState: Int,
): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.statusHandler.handleHandshake(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundHandshakePacket {
            return ServerboundHandshakePacket(
                version = byteBuf.readVarInt(),
                serverAddress = byteBuf.readUtf(255),
                port = byteBuf.readUnsignedShort().toShort(),
                nextState = byteBuf.readVarInt()
            )
        }
    }
}