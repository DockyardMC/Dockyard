package io.github.dockyardmc.socket.encoders

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.writeVarInt
import io.github.dockyardmc.socket.NetworkManager
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import java.lang.Exception

class RawPacketEncoder(val networkManager: NetworkManager): MessageToByteEncoder<Packet>() {

    override fun encode(connection: ChannelHandlerContext, packet: Packet, out: ByteBuf) {
        try {
            println("encoding ${packet::class}")
            val outBuffer = Unpooled.buffer()
            packet.write(outBuffer)
            out.writeVarInt(networkManager.clientPacketRegistry.getIdAndState(packet::class).first)
            out.writeBytes(outBuffer)
            println("sent ${packet::class} (${out.writableBytes()})")
        } catch (exception: Exception) {
            log("There was an error while encoding packet", LogType.ERROR)
            log(exception)
        }
    }
}