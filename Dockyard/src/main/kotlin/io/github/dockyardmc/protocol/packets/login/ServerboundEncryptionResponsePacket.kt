package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readByteArray
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Encryption Response")
@ServerboundPacketInfo(0x01, ProtocolState.LOGIN)
class ServerboundEncryptionResponsePacket(var sharedSecret: ByteArray, var verifyToken: ByteArray): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.loginHandler.handleEncryptionResponse(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundEncryptionResponsePacket {
            val packet = ServerboundEncryptionResponsePacket(byteBuf.readByteArray().clone(), byteBuf.readByteArray().clone())
            return packet
        }
    }
}