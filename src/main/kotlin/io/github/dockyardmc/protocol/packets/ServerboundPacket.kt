package io.github.dockyardmc.protocol.packets

import io.github.dockyardmc.PacketProcessor
import io.netty.channel.ChannelHandlerContext

interface ServerboundPacket {

    fun handle(processor: PacketProcessor, connection: ChannelHandlerContext)
}