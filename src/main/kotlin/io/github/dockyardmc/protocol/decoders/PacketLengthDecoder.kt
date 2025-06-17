package io.github.dockyardmc.protocol.decoders

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.server.ServerMetrics
import io.github.dockyardmc.utils.DataSizeCounter
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class PacketLengthDecoder : ByteToMessageDecoder() {

    override fun decode(connection: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        if (!connection.channel().isActive) return

        buffer.markReaderIndex()
        val length = buffer.readVarInt()

        // reset the reader index if we don't have enough bytes and wait for next part of the message to arrive and check again
        if (length > buffer.readableBytes()) {
            buffer.resetReaderIndex()
            return
        }

        out.add(buffer.retainedSlice(buffer.readerIndex(), length))
        buffer.skipBytes(length)
        // +1 to account for the size byte that is not counted
        ServerMetrics.inboundBandwidth.add(length + 1, DataSizeCounter.Type.BYTE)
        ServerMetrics.totalBandwidth.add(length + 1, DataSizeCounter.Type.BYTE)
    }

    override fun exceptionCaught(connection: ChannelHandlerContext, cause: Throwable) {
        log("Error occurred while decoding frame: ", LogType.ERROR)
        val exception = (if (cause.cause == null) cause else cause.cause) as Exception
        log(exception)
        connection.channel().close().sync()
    }
}