@file:OptIn(ExperimentalStdlibApi::class)

package io.github.dockyardmc.protocol.decoders

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PacketParser
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.WrappedServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.MessageToMessageDecoder
import java.lang.Exception

class PacketDecoder(val processor: PlayerNetworkManager) : MessageToMessageDecoder<ByteBuf>() {

    @OptIn(ExperimentalStdlibApi::class)
    override fun decode(connection: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        if (!connection.channel().isActive) return // connection was closed

        try {
            val packetId = buffer.readVarInt()
            val packetIdByteRep = "0x${packetId.toByte().toHexString()}"

            val size = buffer.readableBytes()

            val packet = PacketParser.parse(packetId, buffer, processor.state)

            // no packet class was found to handle this packet so we skip the bytes and log error
            if (packet == null) {
                log("Received unknown packet with id $packetId ($packetIdByteRep) during phase: ${processor.state.name}", LogType.ERROR)
                buffer.skipBytes(buffer.readableBytes())
                return
            }

            // if the buffer is still readable, there are leftover bytes we didn't read
            if (buffer.isReadable) throw DecoderException("Packet ${packet::class.simpleName} ($packetIdByteRep) was larger than expected, extra bytes: ${buffer.readableBytes()}")

            out.add(WrappedServerboundPacket(packet, size, packetId))

        } catch (ex: Exception) {
            log("Error occurred while decoding packet: ", LogType.ERROR)
            log(ex)
        }
    }
}