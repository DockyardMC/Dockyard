package io.github.dockyardmc.protocol

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.server.ServerMetrics
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketReceivedEvent
import io.github.dockyardmc.events.PlayerDisconnectEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.motd.ServerStatusManager
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.configurations.ConfigurationHandler
import io.github.dockyardmc.protocol.packets.handshake.HandshakeHandler
import io.github.dockyardmc.protocol.packets.login.LoginHandler
import io.github.dockyardmc.protocol.packets.play.PlayHandler
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

    private var innerState = ProtocolState.HANDSHAKE
    var state: ProtocolState
        get() = innerState
        set(value) {
            innerState = value
            val display = if (this::player.isInitialized) player.username else address
            debug("Protocol state for $display changed to $value")
        }

    var statusHandler = HandshakeHandler(this)
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

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log(cause as Exception)
        ctx.flush()
        ctx.close()
    }
}