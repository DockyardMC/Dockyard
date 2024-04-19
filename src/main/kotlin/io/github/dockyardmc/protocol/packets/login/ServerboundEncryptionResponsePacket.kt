package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.PacketProcessor
import io.github.dockyardmc.extentions.readByteArray
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundEncryptionResponsePacket(var sharedSecret: ByteArray, var verifyToken: ByteArray): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext) {
        processor.loginHandler.handleEncryptionResponse(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundEncryptionResponsePacket {
            return ServerboundEncryptionResponsePacket(byteBuf.readByteArray(), byteBuf.readByteArray())
        }
    }
}