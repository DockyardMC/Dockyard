package io.github.dockyardmc.extentions

import cz.lukynka.prettylog.LogType
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketSentEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.server.ServerMetrics
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.channel.ChannelHandlerContext

fun ChannelHandlerContext.sendPacket(packet: ClientboundPacket, processor: PlayerNetworkManager) {

    val context = if (processor.isPlayerInitialized()) getPlayerEventContext(processor.player) else Event.Context.EMPTY
    val event = PacketSentEvent(packet, processor, this, context)
    Events.dispatch(event)
    if (event.cancelled) return

    this.writeAndFlush(packet)
    ServerMetrics.packetsSent++

    val className = packet::class.simpleName
    if (DockyardServer.mutePacketLogs.contains(className)) return
    var message = "<- Sent ${packet::class.simpleName}"
    if (processor.getPlayerOrNull() != null) message += " to ${processor.player} [${processor.state}]"

    debug(message, logType = LogType.NETWORK)
}