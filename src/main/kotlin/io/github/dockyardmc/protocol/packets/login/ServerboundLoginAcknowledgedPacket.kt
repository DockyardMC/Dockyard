package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.channel.ChannelHandlerContext

class ServerboundLoginAcknowledgedPacket: ServerboundPacket {
    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        processor.loginHandler.handleLoginAcknowledge(this, connection)
    }
}