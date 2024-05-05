package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.extentions.readUtfAndLength
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.util.UUID

class ServerboundLoginStartPacket(val name: String, val uuid: UUID): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.loginHandler.handleLoginStart(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundLoginStartPacket {

            val pair = byteBuf.readUtfAndLength()
            val name = pair.first
            val spacing = 4 - pair.second

//            if(name == "LukynkaCZE") {
//                byteBuf.readByte()
//            }

            val uuid = byteBuf.readUUID()

            return ServerboundLoginStartPacket(name, uuid)
        }
    }
}