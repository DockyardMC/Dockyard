package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.readByteArray
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import log

class ServerboundEncryptionResponsePacket(var sharedSecret: ByteArray, var verifyToken: ByteArray): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.loginHandler.handleEncryptionResponse(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundEncryptionResponsePacket {
            log("reading buf in packet.read() (ServerboundEncryptionResponsePacket): buf ref count ${byteBuf.refCnt()}", LogType.TRACE)
            log("Readable bytes left: ${byteBuf.readableBytes()}")
            val packet = ServerboundEncryptionResponsePacket(byteBuf.readByteArray().clone(), byteBuf.readByteArray().clone())
            log("after reading: buf ref count ${byteBuf.refCnt()}", LogType.TRACE)
            log("Readable bytes left after reading: ${byteBuf.readableBytes()}")
            return packet
        }
    }
}