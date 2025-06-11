package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readTextComponent
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf

class ItemNameComponent(val itemName: Component) : DataComponent() {

    constructor(itemName: String): this(itemName.toComponent())

    override fun write(buffer: ByteBuf) {
        buffer.writeTextComponent(itemName)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofNbt(itemName.toNBT()))
    }

    companion object : NetworkReadable<ItemNameComponent> {
        override fun read(buffer: ByteBuf): ItemNameComponent {
            return ItemNameComponent(buffer.readTextComponent())
        }
    }
}