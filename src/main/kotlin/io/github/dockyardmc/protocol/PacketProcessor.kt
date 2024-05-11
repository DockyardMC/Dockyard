package io.github.dockyardmc.protocol

import LogType
import io.github.dockyardmc.TCP
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketReceivedEvent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.configurations.ConfigurationHandler
import io.github.dockyardmc.protocol.packets.login.LoginHandler
import io.github.dockyardmc.protocol.packets.handshake.HandshakeHandler
import io.github.dockyardmc.protocol.packets.play.PlayHandler
import io.ktor.util.network.*
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.ReferenceCountUtil
import log

@Sharable
class PacketProcessor : ChannelInboundHandlerAdapter() {

    private val hideLogsWith = mutableListOf(
        "position"
    )

    private var innerState = ProtocolState.HANDSHAKE
    var encrypted = false

    lateinit var player: Player
    lateinit var address: String

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

    var buffer: ByteBuf = Unpooled.buffer()
    private var bufferReleased = false

    @OptIn(ExperimentalStdlibApi::class)
    override fun channelRead(connection: ChannelHandlerContext, msg: Any) {
        if(!this::address.isInitialized) address = connection.channel().remoteAddress().address
        val buf = msg as ByteBuf
        buffer = buf

        try {
            try {
                while (buf.isReadable) {
                    bufferReleased = false
                    val size = buf.readVarInt()
                    val id = buf.readVarInt()
                    val byte = id.toByte().toHexString()
                    //log("-> 0x$byte[$id] (${buf.readableBytes() + buf.readerIndex()} bytes)", LogType.DEBUG)

                    //Wtf is this why is it having issues with `readBytes` below?
                    if(id == 16) {
                        buf.release()
                        bufferReleased = true
                        return
                    }

                    val data = buf.readBytes(size - 1)

                    val packet = PacketParser.parsePacket(id, data, this, size)

                    if(packet == null) {
                        log("Received unhandled packet with ID $id (0x$byte)", LogType.ERROR)
                        buf.release()
                        bufferReleased = true
                        return
                    }

                    val className = packet::class.simpleName ?: "UnknownClass"
                    if(hideLogsWith.firstOrNull { className.contains(it) } != null) {
                        log("-> Received ${packet::class.simpleName} (0x${byte})", LogType.NETWORK)
                    }

                    Events.dispatch(PacketReceivedEvent(packet, connection, size, id))
                    packet.handle(this, connection, size, id)
                }
            } finally {
                connection.flush()
                if(!bufferReleased) ReferenceCountUtil.release(msg)
                bufferReleased = true
            }
        } catch (ex: Exception) {
            log(ex)
            ReferenceCountUtil.release(msg)
        }
    }

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        log("TCP Handler Added <-> ${ctx.channel().remoteAddress().address}", TCP)
        super.handlerAdded(ctx)
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        log("TCP Handler Removed <-> ${ctx.channel().remoteAddress().address}", TCP)
        if(this::player.isInitialized) PlayerManager.players.remove(player)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}