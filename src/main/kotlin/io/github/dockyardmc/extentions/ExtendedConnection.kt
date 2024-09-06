package io.github.dockyardmc.extentions

import cz.lukynka.prettylog.LogType
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.ServerMetrics
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketSentEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager.getProcessor
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.debug
import io.netty.channel.ChannelHandlerContext

fun ChannelHandlerContext.sendPacket(packet: ClientboundPacket, player: Player? = null) {

    val event = PacketSentEvent(packet, this)
    Events.dispatch(event)
    if(event.cancelled) return

    this.writeAndFlush(packet.asByteBuf())
    ServerMetrics.packetsSent++

    val className = packet::class.simpleName
    if(DockyardServer.mutePacketLogs.contains(className)) return
    var message = "<- Sent ${packet::class.simpleName}"
    if(player != null) message += " to $player [${player.getProcessor().state}]"

    message += " (id: ${packet.id})"
    debug(message, logType = LogType.NETWORK)
}