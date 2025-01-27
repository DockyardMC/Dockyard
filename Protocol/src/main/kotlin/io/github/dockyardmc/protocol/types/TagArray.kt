package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.writers.readList
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.writeList
import io.github.dockyardmc.protocol.writers.writeString
import io.netty.buffer.ByteBuf

class TagArray(val key: String, val tags: List<Tag>): NetworkWritable {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(key)
        buffer.writeList<Tag>(tags, Tag::write)
    }

    companion object {
        fun read(buffer: ByteBuf): TagArray {
            return TagArray(buffer.readString(), buffer.readList(Tag.Companion::read))
        }
    }

}