package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

data class ContainerComponent(val items: List<ItemStack>) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(items, ItemStack::write)
    }

    companion object : NetworkReadable<ContainerComponent> {
        override fun read(buffer: ByteBuf): ContainerComponent {
            return ContainerComponent(buffer.readList(ItemStack::read))
        }
    }
}