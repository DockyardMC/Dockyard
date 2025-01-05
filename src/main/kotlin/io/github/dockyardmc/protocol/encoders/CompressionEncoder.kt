package io.github.dockyardmc.protocol.encoders

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.extentions.readRemainingBytesAsByteArray
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkCompression
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class CompressionEncoder(val processor: PlayerNetworkManager) : MessageToByteEncoder<ByteBuf>() {

    companion object {
        val compressionThreshold get() = DockyardServer.instance.config.networkCompressionThreshold
    }

    override fun encode(connection: ChannelHandlerContext, buffer: ByteBuf, out: ByteBuf) {
        try {
            val dataLength = buffer.readableBytes()
            if (compressionThreshold > dataLength) {
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