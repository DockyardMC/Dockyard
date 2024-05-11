package io.github.dockyardmc.events

import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.channel.ChannelHandlerContext

class PacketReceivedEvent(
    val packet: ServerboundPacket,
    val connection: ChannelHandlerContext,
    val size: Int,
    val id: Int
): Event