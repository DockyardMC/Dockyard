package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Acknowledge Finish Configuration")
@ServerboundPacketInfo(0x03, ProtocolState.CONFIGURATION)
class ServerboundFinishConfigurationAcknowledgePacket: ServerboundPacket {
    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.configurationHandler.handleConfigurationFinishAcknowledge(this, connection)
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundFinishConfigurationAcknowledgePacket {
            buf.readBytes(0)
            return ServerboundFinishConfigurationAcknowledgePacket()
        }
    }
}