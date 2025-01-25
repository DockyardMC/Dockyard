package io.github.dockyardmc.socket.encoders

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.protocol.writers.readRemainingBytesAsByteArray
import io.github.dockyardmc.protocol.writers.writeVarInt
import io.github.dockyardmc.socket.NetworkCompression
import io.github.dockyardmc.socket.NetworkManager
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class CompressionEncoder(val networkManager: NetworkManager) : MessageToByteEncoder<ByteBuf>() {

    override fun encode(connection: ChannelHandlerContext, buffer: ByteBuf, out: ByteBuf) {
        try {
            val dataLength = buffer.readableBytes()
            if (dataLength < networkManager.getServerCompressionThreshold()) {
                out.writeVarInt(0)
                out.writeBytes(buffer)
            } else {
                out.writeVarInt(dataLength)
                out.writeBytes(NetworkCompression.compress(buffer.readRemainingBytesAsByteArray()))
            }
        } catch (exception: Exception) {
            log("There was an error while compressing packet", LogType.ERROR)
            log(exception)
        }
    }
}