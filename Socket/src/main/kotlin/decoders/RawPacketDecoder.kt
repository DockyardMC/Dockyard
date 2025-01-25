@file:OptIn(ExperimentalStdlibApi::class)

package io.github.dockyardmc.socket.decoders

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.protocol.WrappedPacket
import io.github.dockyardmc.protocol.writers.readVarInt
import io.github.dockyardmc.socket.NetworkManager
import io.github.dockyardmc.socket.PacketParser
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.MessageToMessageDecoder
import java.lang.Exception

class RawPacketDecoder(val networkManager: NetworkManager) : MessageToMessageDecoder<ByteBuf>() {

    @OptIn(ExperimentalStdlibApi::class)
    override fun decode(connection: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        if (!connection.channel().isActive) return // connection was closed

        try {
            val packetId = buffer.readVarInt()
            val packetIdByteRep = "0x${packetId.toByte().toHexString()}"
            val state = networkManager.protocolState

            val size = buffer.readableBytes()

            val packet = PacketParser.parse(packetId, buffer, networkManager)

            // no packet class was found to handle this packet, so we skip the bytes and log error
            if (packet == null) {
                log("Received unknown packet with id $packetId ($packetIdByteRep) during phase: ${state.name} [${networkManager.serverPacketRegistry.getSkippedFromIdOrNull(packetId, state)}]", LogType.ERROR)
                buffer.skipBytes(buffer.readableBytes())
                return
            }

            // if the buffer is still readable, there are leftover bytes we didn't read
            if (buffer.isReadable) throw DecoderException("Packet ${packet::class.simpleName} ($packetIdByteRep) was larger than expected, extra bytes: ${buffer.readableBytes()}")

            out.add(WrappedPacket(packet, size, packetId))

        } catch (ex: Exception) {
            log("Error occurred while decoding packet: ", LogType.ERROR)
            log(ex)
        }
    }
}