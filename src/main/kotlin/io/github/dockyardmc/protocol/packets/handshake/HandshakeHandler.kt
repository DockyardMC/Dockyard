package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerListPingEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.motd.*
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.netty.channel.ChannelHandlerContext
import java.time.Instant

class HandshakeHandler(val processor: PlayerNetworkManager): PacketHandler(processor) {

    fun handlePing(packet: ServerboundPingRequestPacket, connection: ChannelHandlerContext) {
        val out = ClientboundPingResponsePacket(Instant.now().toEpochMilli())
        connection.sendPacket(out, processor)
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

        val serverStatus = ServerStatusManager.getCache()
        Events.dispatch(ServerListPingEvent(serverStatus))

        val json = serverStatus.toJson()
        val out = ClientboundStatusResponsePacket(json)

        connection.sendPacket(out, processor)
    }
}