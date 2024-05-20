@file:OptIn(ExperimentalStdlibApi::class)
package io.github.dockyardmc.protocol

import LogType
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.packets.UnprocessedPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import log

@OptIn(ExperimentalStdlibApi::class)
object PacketDecoder {

    fun decode(buffer: ByteBuf, connection: ChannelHandlerContext): UnprocessedPacket? {
        buffer.markReaderIndex()
        while(buffer.isReadable) {

            val packetId = buffer.readVarInt()
            // -1 To exclude the packetId field that has already been read. packet size field is not included in the size
            val packetSize = buffer.readVarInt() - 1
            val packetIdByteRep = "0x${packetId.toByte().toHexString()}"

            if (buffer.readableBytes() != packetSize) {
                log(
                    "Packet ID $packetId ($packetIdByteRep) is not the expected size (expected $packetSize, is ${buffer.readableBytes()})",
                    LogType.WARNING
                )
            }

            val packetData = buffer.readBytes(packetSize)
            buffer.clear()
            buffer.release()
            connection.flush()
            return UnprocessedPacket(packetId, packetSize, packetData)
        }
        return null
    }

    fun handleDecodingException(connection: ChannelHandlerContext, buffer: ByteBuf, cause: Exception) {
        log(cause)
        buffer.clear()
        buffer.release()
        connection.flush()
    }
}