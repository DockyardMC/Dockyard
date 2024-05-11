package io.github.dockyardmc.protocol.packets.handshake

import LogType
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerListPingEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.motd.Players
import io.github.dockyardmc.motd.ServerStatus
import io.github.dockyardmc.motd.Version
import io.github.dockyardmc.motd.toJson
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
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

        val serverStatus = ServerStatus(
            version = Version(
                name = "1.20.4",
                protocol = 765,
            ),
            players = Players(
                max = 727,
                online = PlayerManager.players.size,
                sample = mutableListOf(),
            ),
            description = "§bDockyardMC §8| §7Kotlin Server Implementation",
            enforceSecureChat = false,
            previewsChat = false,
            favicon = "data:image/png;base64,$base64EncodedIcon"
        )

        Events.dispatch(ServerListPingEvent(serverStatus))

        val json = serverStatus.toJson()
        val out = ClientboundStatusResponsePacket(json)

        connection.sendPacket(out)
    }
}