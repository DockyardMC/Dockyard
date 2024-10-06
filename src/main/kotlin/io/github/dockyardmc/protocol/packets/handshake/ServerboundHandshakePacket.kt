package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket

@WikiVGEntry("Handshake")
@ServerboundPacketInfo(0x00, ProtocolState.HANDSHAKE)
class ServerboundHandshakePacket(
    val version: Int,
    val serverAddress: String,
    val port: Short,
    val nextState: Int,
): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.statusHandler.handleHandshake(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundHandshakePacket {
            return ServerboundHandshakePacket(
                version = byteBuf.readVarInt(),
                serverAddress = byteBuf.readString(),
                port = byteBuf.readUnsignedShort().toShort(),
                nextState = byteBuf.readVarInt()
            )
        }
    }
}