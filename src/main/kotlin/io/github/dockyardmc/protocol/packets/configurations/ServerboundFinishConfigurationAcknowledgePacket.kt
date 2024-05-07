package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.channel.ChannelHandlerContext

class ServerboundFinishConfigurationAcknowledgePacket: ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.configurationHandler.handleConfigurationFinishAcknowledge(this, connection)
    }
}