package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readByteArray
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.writeByteArray
import io.github.dockyardmc.protocol.writers.writeString
import io.netty.buffer.ByteBuf

class ClientboundConfigurationCookieStorePacket(
    val key: String,
    val data: ByteArray
): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(key)
        buffer.writeByteArray(data)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundConfigurationCookieStorePacket {
            return ClientboundConfigurationCookieStorePacket(
                buffer.readString(),
                buffer.readByteArray()
            )
        }
    }
}