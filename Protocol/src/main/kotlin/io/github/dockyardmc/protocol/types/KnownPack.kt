package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.protocol.ProtocolWritable
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.writeString
import io.netty.buffer.ByteBuf

data class KnownPack(
    val namespace: String,
    val id: String,
    val version: String
): ProtocolWritable {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(namespace)
        buffer.writeString(id)
        buffer.writeString(version)
    }

    companion object {
        fun read(buffer: ByteBuf): KnownPack {
            return KnownPack(
                buffer.readString(),
                buffer.readString(),
                buffer.readString()
            )
        }
    }
}