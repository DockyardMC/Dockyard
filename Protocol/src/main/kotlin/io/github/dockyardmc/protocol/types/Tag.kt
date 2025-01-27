package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.writers.*
import io.netty.buffer.ByteBuf

data class Tag(
    val tagName: String,
    val tags: List<Int>
): NetworkWritable {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(tagName)
        buffer.writeList<Int>(tags, ByteBuf::writeVarInt)
    }

    companion object {
        fun read(buffer: ByteBuf): Tag {
            return Tag(
                buffer.readString(),
                buffer.readList<Int>(ByteBuf::readVarInt)
            )
        }
    }
}