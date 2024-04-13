package io.github.dockyard.server

import io.github.dockyard.server.packets.protocol.PacketWrapper
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter


class PacketProcessingHandler: ChannelInboundHandlerAdapter() {
    val handler = PacketHandler(this)

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        println("New Handler!")
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        println("Removed handler!")
        super.handlerRemoved(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) { // (2)
        val buf = (msg as ByteBuf)
        val wrapper = PacketWrapper(handler.protocol, buf)
        println("Packet recived with ID '${wrapper.packetId}'")
        wrapper.data.handle(handler)
        buf.release()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace()
        ctx.close()
    }
}