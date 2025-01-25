package io.github.dockyardmc.protocol.packets.login.serverbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.*
import io.netty.buffer.ByteBuf

class ServerboundLoginCookieResponsePacket(val key: String, val data: ByteArray?): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(key)
        buffer.writeOptional<ByteArray>(data, ByteBuf::writeByteArray)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundLoginCookieResponsePacket {
            return ServerboundLoginCookieResponsePacket(
                buffer.readString(),
                buffer.readOptional<ByteArray>(ByteBuf::readByteArray)
            )
        }
    }
}