package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

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