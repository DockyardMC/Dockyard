package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf

class WrittenBookContentComponent(
    val title: WritableBookContent.FilteredText,
    val author: String,
    val generation: Int,
    val pages: List<WritableBookContent.FilteredText>,
    val resolved: Boolean
) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        title.write(buffer)
        buffer.writeString(author)
        buffer.writeVarInt(generation)
        buffer.writeList(pages, WritableBookContent.FilteredText::write)
        buffer.writeBoolean(resolved)
    }

    override fun hashStruct(): HashHolder {
        return unsupported(this)
    }

    companion object : NetworkReadable<WrittenBookContentComponent> {

        override fun read(buffer: ByteBuf): WrittenBookContentComponent {
            return WrittenBookContentComponent(
                WritableBookContent.FilteredText.read(buffer),
                buffer.readString(),
                buffer.readVarInt(),
                buffer.readList(WritableBookContent.FilteredText::read),
                buffer.readBoolean()
            )
        }
    }
}