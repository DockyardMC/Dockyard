package io.github.dockyardmc.socket.encoders

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.protocol.writers.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class PacketLengthEncoder: MessageToByteEncoder<ByteBuf>() {

    override fun encode(connection: ChannelHandlerContext, buffer: ByteBuf, out: ByteBuf) {
        try {
            out.writeVarInt(buffer.readableBytes())
            out.writeBytes(buffer)
        } catch (exception: Exception) {
            log("There was an error while encoding packet length", LogType.ERROR)
            log(exception)
        }
    }
}