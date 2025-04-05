package io.github.dockyardmc.protocol.encoders

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.server.ServerMetrics
import io.github.dockyardmc.utils.DataSizeCounter
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class PacketLengthEncoder: MessageToByteEncoder<ByteBuf>() {

    override fun encode(connection: ChannelHandlerContext, buffer: ByteBuf, out: ByteBuf) {
        try {
            val size = buffer.readableBytes()
            out.writeVarInt(size)
            out.writeBytes(buffer)
            ServerMetrics.outboundBandwidth.add(size, DataSizeCounter.Type.BYTE)
            ServerMetrics.totalBandwidth.add(size, DataSizeCounter.Type.BYTE)
        } catch (exception: Exception) {
            log("There was an error while encoding packet length", LogType.ERROR)
            log(exception)
        }
    }
}