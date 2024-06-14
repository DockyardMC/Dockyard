package io.github.dockyardmc.protocol.packets.login

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readByteArray
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Encryption Response")
@ServerboundPacketInfo(0x01, ProtocolState.LOGIN)
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