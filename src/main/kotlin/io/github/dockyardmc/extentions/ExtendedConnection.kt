package io.github.dockyardmc.extentions

import LogType
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketSentEvent
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.netty.channel.ChannelHandlerContext
import log

fun ChannelHandlerContext.sendPacket(packet: ClientboundPacket) {
    Events.dispatch(PacketSentEvent(packet))
    log("<- Sent ${packet::class.simpleName}", LogType.NETWORK)
    this.write(packet.asByteBuf())
}