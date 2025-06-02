package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readTextComponent
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.scroll.Component
import io.netty.buffer.ByteBuf

class ItemNameComponent(val itemName: Component): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeTextComponent(itemName)
    }

    companion object: NetworkReadable<ItemNameComponent> {
        override fun read(buffer: ByteBuf): ItemNameComponent {
            return ItemNameComponent(buffer.readTextComponent())
        }
    }
}