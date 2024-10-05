package io.github.dockyardmc.protocol.decoders

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class FrameDecoder : ByteToMessageDecoder() {

    override fun decode(connection: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        if (!connection.channel().isActive) return

        buffer.markReaderIndex()
        val length = buffer.readVarInt()

        // reset the reader index if we dont have enough bytes and wait for next part of the message to arrive and check agian
        if(length > buffer.readableBytes()) {
            buffer.resetReaderIndex()
            return
        }

        out.add(buffer.retainedSlice(buffer.readerIndex(), length))
        buffer.skipBytes(length)
    }

    override fun exceptionCaught(connection: ChannelHandlerContext, cause: Throwable) {
        log("Error occurred while decoding frame: ", LogType.ERROR)
        log(cause as Exception)
        connection.channel().close().sync()
    }
}