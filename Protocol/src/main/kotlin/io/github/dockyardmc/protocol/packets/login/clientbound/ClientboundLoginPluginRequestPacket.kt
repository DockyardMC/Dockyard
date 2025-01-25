package io.github.dockyardmc.protocol.packets.login.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.readVarInt
import io.github.dockyardmc.protocol.writers.writeString
import io.github.dockyardmc.protocol.writers.writeVarInt
import io.netty.buffer.ByteBuf

class ClientboundLoginPluginRequestPacket(
    val messageId: Int,
    val channel: String,
    val data: ByteBuf
): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(messageId)
        buffer.writeString(channel)
        buffer.writeBytes(data)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundLoginPluginRequestPacket {
            return ClientboundLoginPluginRequestPacket(
                buffer.readVarInt(),
                buffer.readString(),
                buffer.readBytes(buffer.readableBytes())
            )
        }
    }

}