package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.netty.channel.ChannelHandlerContext

@EventDocumentation("server sends packet to client", true)
class PacketSentEvent(
    var packet: ClientboundPacket,
    val processor: PlayerNetworkManager,
    var connection: ChannelHandlerContext
) : CancellableEvent() {
    override val context = elements(processor)
}