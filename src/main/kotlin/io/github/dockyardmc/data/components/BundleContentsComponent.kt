package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf

class BundleContentsComponent(val contents: List<ItemStack>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(contents, ItemStack::write)
    }

    companion object : NetworkReadable<BundleContentsComponent> {
        override fun read(buffer: ByteBuf): BundleContentsComponent {
            return BundleContentsComponent(buffer.readList(ItemStack::read))
        }
    }

}