package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.channel.ChannelHandlerContext

@EventDocumentation("server receives packet from client", true)
class PacketReceivedEvent(
    val packet: ServerboundPacket,
    val connection: ChannelHandlerContext,
    val size: Int,
    val id: Int
): CancellableEvent()