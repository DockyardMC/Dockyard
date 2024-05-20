package io.github.dockyardmc.protocol.packets.handshake

import LogType
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerListPingEvent
import io.github.dockyardmc.extentions.component
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.motd.*
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scroll.ComponentSerializer
import io.github.dockyardmc.utils.Branding
import io.ktor.util.network.*
import io.netty.channel.ChannelHandlerContext
import log
import java.io.File
import java.time.Instant
import java.util.*

class HandshakeHandler(val processor: PacketProcessor): PacketHandler(processor) {

    fun handlePing(packet: ServerboundPingRequestPacket, connection: ChannelHandlerContext) {
        log("Received ping with time ${packet.time}", LogType.DEBUG)
        val out = ClientboundPingResponsePacket(Instant.now().toEpochMilli())
        connection.sendPacket(out)
    }

    fun handleHandshake(packet: ServerboundHandshakePacket, connection: ChannelHandlerContext) {

        val handshakeState = packet.nextState
        log("Handshake from ${connection.channel().remoteAddress().address} with version ${packet.version}", LogType.DEBUG)

        if(handshakeState == 2) {
            processor.loginHandler.handleHandshake(packet, connection)
            return
        }

        processor.state = ProtocolState.STATUS
    }

    fun handleStatusRequest(packet: ServerboundStatusRequestPacket, connection: ChannelHandlerContext) {

        val base64EncodedIcon = Base64.getEncoder().encode(File("./icon.png").readBytes()).decodeToString()

        val players = mutableListOf<ServerListPlayer>()
        PlayerManager.players.forEach {
            players.add(ServerListPlayer(it.username, it.uuid.toString()))
        }

        val serverStatus = DockyardServer.defaultMotd

        Events.dispatch(ServerListPingEvent(serverStatus))

        val json = serverStatus.toJson()
        val out = ClientboundStatusResponsePacket(json)

        connection.sendPacket(out)
    }
}