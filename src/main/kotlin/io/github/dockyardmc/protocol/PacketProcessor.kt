package io.github.dockyardmc.protocol

import LogType
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.TCP
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketReceivedEvent
import io.github.dockyardmc.events.PlayerDisconnectEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.profiler.Profiler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.configurations.ConfigurationHandler
import io.github.dockyardmc.protocol.packets.login.LoginHandler
import io.github.dockyardmc.protocol.packets.handshake.HandshakeHandler
import io.github.dockyardmc.protocol.packets.play.PlayHandler
import io.ktor.util.network.*
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import log

@Sharable
class PacketProcessor : ChannelInboundHandlerAdapter() {

    private var innerState = ProtocolState.HANDSHAKE
    var encrypted = false

    lateinit var player: Player
    lateinit var address: String
    var playerProtocolVersion: Int = 0

    var respondedToLastKeepAlive = true

    var state: ProtocolState
        get() = innerState
        set(value) {
            innerState = value
            val display = if(this::player.isInitialized) player.username else address
            log("Protocol state for $display changed to $value")
        }

    var statusHandler = HandshakeHandler(this)
    var loginHandler = LoginHandler(this)
    var configurationHandler = ConfigurationHandler(this)
    var playHandler = PlayHandler(this)

    @OptIn(ExperimentalStdlibApi::class)
    override fun channelRead(connection: ChannelHandlerContext, msg: Any) {
        val profiler = Profiler()

        if(!this::address.isInitialized) address = connection.channel().remoteAddress().address
        val buf = msg as ByteBuf

        try {
            try {
                profiler.start("Read Packet Buf", 20)
                while (buf.isReadable) {
                    buf.markReaderIndex()
                    val packetSize = buf.readVarInt() - 1
                    val packetId = buf.readVarInt()
                    val packetIdByteRep = packetId.toByte().toHexString()
//                    log("id: $packetId size: $packetSize (0x$packetIdByteRep) (netty readable bytes: ${buf.readableBytes()})")

                    if(packetId == 16 && state == ProtocolState.PLAY) {
                        val channel = buf.readUtf()
                        log("Ignoring custom payload packet for $channel", LogType.WARNING)
                        return
                    }

                    val packetData = buf.readBytes(packetSize)

                    val packet = PacketParser.parsePacket(packetId, packetData, this, packetSize)
                        ?: throw UnknownPacketException("Received unhandled packet with ID $packetId (0x$packetIdByteRep)")

                    val className = packet::class.simpleName ?: packet::class.toString()
                    if(!DockyardServer.mutePacketLogs.contains(className)) {
                        log("-> Received $className (0x${packetIdByteRep})", LogType.NETWORK)
                    }

                    Events.dispatch(PacketReceivedEvent(packet, connection, packetSize, packetId))
                    packet.handle(this, connection, packetSize, packetId)
                }
            } finally {
                buf.clear()
                buf.release()
                connection.flush()
                profiler.end()
            }
        } catch (ex: Exception) {
            handleException(connection, buf, ex)
        }
    }

    fun clearBuffer(connection: ChannelHandlerContext, buffer: ByteBuf) {
        buffer.clear()
        buffer.resetReaderIndex()
        buffer.release()
        connection.flush()
    }

    fun handleException(connection: ChannelHandlerContext, buffer: ByteBuf, exception: Exception) {
        log(exception)
        clearBuffer(connection, buffer)
    }

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        log("TCP Handler Added <-> ${ctx.channel().remoteAddress().address}", TCP)
        super.handlerAdded(ctx)
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        log("TCP Handler Removed <-> ${ctx.channel().remoteAddress().address}", TCP)
        if(this::player.isInitialized) {
            PlayerManager.remove(player)
            Events.dispatch(PlayerDisconnectEvent(player))
            if(player.isFullyInitialized) {
                Events.dispatch(PlayerLeaveEvent(player))
            }
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
//        cause.printStackTrace()
//        ctx.close()
    }
}