package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.extentions.readUtfAndLength
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import log
import java.util.UUID

@ServerboundPacketInfo(0, ProtocolState.LOGIN)
class ServerboundLoginStartPacket(val name: String, val uuid: UUID): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.loginHandler.handleLoginStart(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundLoginStartPacket {
            log("reading buf in packet.read() (ServerboundLoginStartPacket): buf ref count ${byteBuf.refCnt()}", LogType.TRACE)
            log("Readable bytes left: ${byteBuf.readableBytes()}")

            val name = byteBuf.readUtf(16)
            log("read string from the buf: buf ref count ${byteBuf.refCnt()}", LogType.TRACE)

            val uuid = byteBuf.readUUID()
            log("read UUID from the buf: buf ref count ${byteBuf.refCnt()}", LogType.TRACE)
            log("Readable bytes left after reading: ${byteBuf.readableBytes()}")

            return ServerboundLoginStartPacket(name, uuid)
        }
    }
}