package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Serverbound Plugin Message (configuration)")
@ServerboundPacketInfo(0x02, ProtocolState.CONFIGURATION)
class ServerboundConfigurationPluginMessagePacket(val channel: String, val data: ByteBuf) : ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.configurationHandler.handlePluginMessage(this, connection)
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundConfigurationPluginMessagePacket {
            val channel = byteBuf.readString(32767)
            val data = byteBuf.readBytes(byteBuf.readableBytes())
            return ServerboundConfigurationPluginMessagePacket(channel, data)
        }
    }
}