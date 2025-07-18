package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.protocol.writeOptional
import io.netty.buffer.ByteBuf

class WritableBookContent(val pages: List<FilteredText>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(pages, FilteredText::write)
    }

    override fun hashStruct(): HashHolder {
        return unsupported(this)
    }

    companion object : NetworkReadable<WritableBookContent> {

        val EMPTY = WritableBookContent(listOf())

        override fun read(buffer: ByteBuf): WritableBookContent {
            return WritableBookContent(buffer.readList(FilteredText::read))
        }
    }

    data class FilteredText(val text: String, val filtered: String? = null) : NetworkWritable {

        override fun write(buffer: ByteBuf) {
            buffer.writeString(text)
            buffer.writeOptional(filtered, ByteBuf::writeString)
        }

        companion object : NetworkReadable<FilteredText> {
            override fun read(buffer: ByteBuf): FilteredText {
                return FilteredText(buffer.readString(), buffer.readOptional(ByteBuf::readString))
            }
        }

    }
}