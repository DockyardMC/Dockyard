package io.github.dockyardmc

import LogType
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketReceivedEvent
import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.extentions.readUtf
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PacketParser
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.login.LoginHandler
import io.github.dockyardmc.protocol.packets.status.ServerboundHandshakePacket
import io.github.dockyardmc.protocol.packets.status.StatusPacketHandler
import io.ktor.util.network.*
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.ReferenceCountUtil
import log

class PacketProcessor : ChannelInboundHandlerAdapter() {

    private var innerState = ProtocolState.HANDSHAKE
    var state: ProtocolState
        get() = innerState
        set(value) {
            innerState = value
            log("Protocol state changed to $value")
        }

    var statusHandler = StatusPacketHandler(this)
    var loginHandler = LoginHandler(this)

    var buffer: ByteBuf = Unpooled.buffer()

    override fun channelRead(connection: ChannelHandlerContext, msg: Any) {
        val buf = msg as ByteBuf
        buffer = buf

        try {
            while (buf.isReadable) {
                val size = buf.readVarInt()
                val id = buf.readVarInt()

                val packet = PacketParser.parsePacket(id, buf, this)

                if(packet == null) {
                    log("Received unhandled packet with ID $id", LogType.ERROR)
                    continue
                }
                log("Received ${packet::class.simpleName} (Size ${size})", LogType.NETWORK)
                packet.handle(this, connection)
                Events.dispatch(PacketReceivedEvent(packet))
            }
        } finally {
            connection.flush()
            ReferenceCountUtil.release(msg)
        }
    }

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        log("TCP Handler Added <-> ${ctx.channel().remoteAddress().address}", TCP)
        super.handlerAdded(ctx)
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext) {

        log("TCP Handler Removed <-> ${ctx.channel().remoteAddress().address}", TCP)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}