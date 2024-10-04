@file:OptIn(ExperimentalStdlibApi::class)

package io.github.dockyardmc.protocol.decoders

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PacketParser
import io.github.dockyardmc.protocol.PacketProcessor
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.MessageToMessageDecoder
import java.lang.Exception

class PacketDecoder(val processor: PacketProcessor): MessageToMessageDecoder<ByteBuf>() {

    override fun decode(connection: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        if(!connection.channel().isActive) return

        try {
            val packetId = buffer.readVarInt()
            val packetIdByteRep = "0x${packetId.toByte().toHexString()}"

            val packet = PacketParser.parse(packetId, buffer, processor.state)
            if(packet != null) {
                if(buffer.isReadable) {
                    throw DecoderException("Packet ${packet::class.simpleName} ($packetIdByteRep) was larger than expected, extra bytes: ${buffer.readableBytes()}")
                }
                out.add(packet)
            } else {
                buffer.skipBytes(buffer.readableBytes())
            }

        } catch (ex: Exception) {
            log("Error occurred while decoding packet: ", LogType.ERROR)
            log(ex)
        }
    }
}