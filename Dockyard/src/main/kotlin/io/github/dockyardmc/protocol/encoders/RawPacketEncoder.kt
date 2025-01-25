package io.github.dockyardmc.protocol.encoders

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import java.lang.Exception

class RawPacketEncoder: MessageToByteEncoder<ClientboundPacket>() {

    override fun encode(connection: ChannelHandlerContext, packet: ClientboundPacket, out: ByteBuf) {
        try {
            out.writeVarInt(packet.id!!)
            out.writeBytes(packet.data.copy())
        } catch (exception: Exception) {
            log("There was an error while encoding packet", LogType.ERROR)
            log(exception)
        }
    }
}