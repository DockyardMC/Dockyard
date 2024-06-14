package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Serverbound Plugin Message (configuration)")
@ServerboundPacketInfo(0x02, ProtocolState.CONFIGURATION)
class ServerboundPluginMessagePacket(var channel: String, var data: String): ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.configurationHandler.handlePluginMessage(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPluginMessagePacket {
            val channel = buf.readUtf()
            val leftBits = buf.readableBytes()
            val restOfData = buf.readBytes(buf.readableBytes())

            return ServerboundPluginMessagePacket(channel, "")
        }
    }
}