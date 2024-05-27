package io.github.dockyardmc.extentions

import LogType
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketSentEvent
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.netty.channel.ChannelHandlerContext
import log
import org.jglrxavpok.hephaistos.mca.pack

fun ChannelHandlerContext.sendPacket(packet: ClientboundPacket) {
    Events.dispatch(PacketSentEvent(packet))
    this.write(packet.asByteBuf())

    val className = packet::class.simpleName
    if(DockyardServer.mutePacketLogs.contains(className)) return
    log("<- Sent $className", LogType.NETWORK)
}