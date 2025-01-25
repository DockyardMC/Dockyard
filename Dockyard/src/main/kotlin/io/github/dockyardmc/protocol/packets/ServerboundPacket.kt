package io.github.dockyardmc.protocol.packets

import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.netty.channel.ChannelHandlerContext

interface ServerboundPacket {
     fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int)
}