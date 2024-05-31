package io.github.dockyardmc.extentions

import LogType
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketSentEvent
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.netty.channel.ChannelHandlerContext
import log

fun ChannelHandlerContext.sendPacket(packet: ClientboundPacket) {
    Events.dispatch(PacketSentEvent(packet))
    this.writeAndFlush(packet.asByteBuf())

    val className = packet::class.simpleName
    if(DockyardServer.mutePacketLogs.contains(className)) return
    log("<- Sent $className", LogType.NETWORK)
}