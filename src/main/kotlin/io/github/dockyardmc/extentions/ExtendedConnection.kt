package io.github.dockyardmc.extentions

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketSentEvent
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.netty.channel.ChannelHandlerContext

fun ChannelHandlerContext.sendPacket(packet: ClientboundPacket) {
    Events.dispatch(PacketSentEvent(packet))
    this.write(packet.asByteBuf())
}