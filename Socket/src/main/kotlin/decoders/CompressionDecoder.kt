package io.github.dockyardmc.protocol.decoders

import io.github.dockyardmc.protocol.writers.readRemainingBytesAsByteArray
import io.github.dockyardmc.protocol.writers.readVarInt
import io.github.dockyardmc.socket.NetworkCompression
import io.github.dockyardmc.socket.NetworkManager
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class CompressionDecoder(val networkManager: NetworkManager) : ByteToMessageDecoder() {

    override fun decode(connection: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        if (!connection.channel().isActive) return
        val dataLength = buffer.readVarInt()

        if (dataLength == 0) {
            out.add(buffer.retainedSlice())
            buffer.skipBytes(buffer.readableBytes())
            return
        }

        val compressed = buffer.readRemainingBytesAsByteArray()
        val uncompressed = Unpooled.wrappedBuffer(NetworkCompression.decompress(compressed))
        out.add(uncompressed)
    }
}