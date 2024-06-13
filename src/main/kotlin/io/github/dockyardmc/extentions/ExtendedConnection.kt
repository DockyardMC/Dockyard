package io.github.dockyardmc.extentions

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketSentEvent
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.netty.channel.ChannelHandlerContext

fun ChannelHandlerContext.sendPacket(packet: ClientboundPacket) {

    val event = PacketSentEvent(packet, this)
    Events.dispatch(event)
    if(event.cancelled) return

    this.writeAndFlush(packet.asByteBuf())

    val className = packet::class.simpleName
    if(DockyardServer.mutePacketLogs.contains(className)) return
}