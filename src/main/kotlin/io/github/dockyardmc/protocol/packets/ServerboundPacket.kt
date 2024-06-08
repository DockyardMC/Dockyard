package io.github.dockyardmc.protocol.packets

import io.github.dockyardmc.protocol.PacketProcessor
import io.netty.channel.ChannelHandlerContext

interface ServerboundPacket {
     fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int)
}