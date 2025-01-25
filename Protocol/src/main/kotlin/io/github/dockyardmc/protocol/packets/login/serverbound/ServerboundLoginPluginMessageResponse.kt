package io.github.dockyardmc.protocol.packets.login.serverbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.*
import io.netty.buffer.ByteBuf

class ServerboundLoginPluginMessageResponse(
    val messageId: Int,
    val data: ByteBuf?
): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(messageId)
        buffer.writeOptional<ByteBuf>(data, ByteBuf::writeByteBuf)
    }
    
    companion object {
        fun read(buffer: ByteBuf): ServerboundLoginPluginMessageResponse {
            return ServerboundLoginPluginMessageResponse(
                buffer.readVarInt(),
                buffer.readOptional<ByteBuf> { buffer.readBytes(buffer.readableBytes()) }
            )
        }
    }

}