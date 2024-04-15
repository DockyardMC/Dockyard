package io.github.dockyardmc

import LogType
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketReceivedEvent
import io.github.dockyardmc.extentions.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.github.dockyardmc.protocol.PacketParser
import io.github.dockyardmc.protocol.packets.status.StatusPacketHandler
import log

class PacketProcessor : ChannelInboundHandlerAdapter() {

    var handler = StatusPacketHandler()

    override fun channelRead(connection: ChannelHandlerContext, msg: Any) {
        val buf = msg as ByteBuf
        buf.readVarInt()

        val id = buf.readVarInt()

        val packet = PacketParser.parsePacket(id, buf)
        if(packet == null) log("Received unhandled packet with ID $id", LogType.ERROR)
        packet?.let {
            log("Received packet with ID $id", LogType.NETWORK)
            it.handle(this, connection)
            Events.dispatch(PacketReceivedEvent(it))
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}