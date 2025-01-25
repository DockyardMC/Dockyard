package io.github.dockyardmc.protocol

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.server.ServerMetrics
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketReceivedEvent
import io.github.dockyardmc.events.PlayerDisconnectEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.motd.ServerStatusManager
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.kick.getSystemKickMessage
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.configurations.ConfigurationHandler
import io.github.dockyardmc.protocol.packets.login.ClientboundLoginDisconnectPacket
import io.github.dockyardmc.protocol.packets.login.LoginHandler
import io.github.dockyardmc.protocol.packets.play.PlayHandler
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundDisconnectPacket
import io.github.dockyardmc.resourcepack.ResourcepackManager
import io.github.dockyardmc.utils.debug
import io.ktor.util.network.*
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class PlayerNetworkManager : ChannelInboundHandlerAdapter() {

    var encryptionEnabled = false
    var compressionEnabled = false

    lateinit var player: Player
    lateinit var address: String
    var playerProtocolVersion: Int = 0
    var respondedToLastKeepAlive = true

    var state: ProtocolState = ProtocolState.HANDSHAKE

    var loginHandler = LoginHandler(this)
    var configurationHandler = ConfigurationHandler(this)
    var playHandler = PlayHandler(this)

    override fun channelRead(connection: ChannelHandlerContext, msg: Any) {
        if (!this::address.isInitialized) address = connection.channel().remoteAddress().address

        val packet = msg as WrappedServerboundPacket

        val className = packet.packet::class.simpleName
        ServerMetrics.packetsReceived++
        if (!DockyardServer.mutePacketLogs.contains(className)) {
            debug("-> Received $className", logType = LogType.NETWORK)
        }

        val event = PacketReceivedEvent(packet.packet, this, connection, packet.size, packet.id)
        Events.dispatch(event)
        if (event.cancelled) return

        event.packet.handle(this, connection, packet.size, packet.id)
    }

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        super.handlerAdded(ctx)
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        if (isPlayerInitialized()) {
            player.isConnected = false
            player.team.value = null
            PlayerManager.remove(player)
            ResourcepackManager.pending.toList().filter { it.player == player }.forEach { pack ->
                ResourcepackManager.pending.remove(pack)
            }
            Events.dispatch(PlayerDisconnectEvent(player))
            if (player.isFullyInitialized) {
                ServerStatusManager.updateCache()
                Events.dispatch(PlayerLeaveEvent(player))
            }
            player.dispose()
        }
    }

    fun kick(message: String, connection: ChannelHandlerContext) {
        val formattedMessage = getSystemKickMessage(message)
        val packet = when(state) {
            ProtocolState.HANDSHAKE,
            ProtocolState.STATUS,
            ProtocolState.LOGIN -> ClientboundLoginDisconnectPacket(formattedMessage)

            ProtocolState.CONFIGURATION,
            ProtocolState.PLAY -> ClientboundDisconnectPacket(formattedMessage)
        }

        connection.sendPacket(packet, this)
        connection.close()
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    fun isPlayerInitialized(): Boolean {
        return ::player.isInitialized
    }

    fun getPlayerOrNull(): Player? {
        return if (isPlayerInitialized()) player else null
    }

    fun getPlayerOrThrow(): Player {
        return if (isPlayerInitialized()) player else throw UninitializedPropertyAccessException("Player has not been initialized yet")
    }

    override fun exceptionCaught(connection: ChannelHandlerContext, cause: Throwable) {
        log(cause as Exception)
        if(player.isFullyInitialized) {
            kick(getSystemKickMessage("There was an error while writing packet: ${cause.message}"), connection)
        }
        connection.flush()
        connection.close()
    }
}