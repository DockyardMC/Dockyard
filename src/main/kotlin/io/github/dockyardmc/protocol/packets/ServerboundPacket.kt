package io.github.dockyardmc.protocol.packets

import io.github.dockyardmc.PacketProcessor
import io.netty.channel.ChannelHandlerContext

open class ServerboundPacket {

    open fun handle(processor: PacketProcessor, connection: ChannelHandlerContext) {

    }
}