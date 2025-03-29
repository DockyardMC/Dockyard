package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class ItemModelComponent(val itemModel: String): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(itemModel)
    }

    companion object: NetworkReadable<ItemModelComponent> {
        override fun read(buffer: ByteBuf): ItemModelComponent {
            return ItemModelComponent(buffer.readString())
        }
    }
}