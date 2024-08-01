package io.github.dockyardmc.protocol.packets.handshake

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerListPingEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.motd.*
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.ktor.util.network.*
import io.netty.channel.ChannelHandlerContext
import java.io.File
import java.time.Instant
import java.util.*

class HandshakeHandler(val processor: PacketProcessor): PacketHandler(processor) {

    fun handlePing(packet: ServerboundPingRequestPacket, connection: ChannelHandlerContext) {
        val out = ClientboundPingResponsePacket(Instant.now().toEpochMilli())
        connection.sendPacket(out)
    }

    fun handleHandshake(packet: ServerboundHandshakePacket, connection: ChannelHandlerContext) {

        val handshakeState = packet.nextState

        if(handshakeState == 2) {
            processor.loginHandler.handleHandshake(packet, connection)
            return
        }

        processor.state = ProtocolState.STATUS
    }

    fun handleStatusRequest(packet: ServerboundStatusRequestPacket, connection: ChannelHandlerContext) {

        val players = mutableListOf<ServerListPlayer>()
        PlayerManager.players.forEach {
            players.add(ServerListPlayer(it.username, it.uuid.toString()))
        }

        val serverStatus = defaultMotd
        Events.dispatch(ServerListPingEvent(serverStatus))

        val json = serverStatus.toJson()
        val out = ClientboundStatusResponsePacket(json)

        connection.sendPacket(out)
    }
}