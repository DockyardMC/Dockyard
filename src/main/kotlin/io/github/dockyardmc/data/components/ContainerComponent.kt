package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf

data class ContainerComponent(val items: List<ItemStack>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(items, ItemStack::write)
    }

    override fun hashStruct(): HashHolder {
        val finalHash = mutableListOf<Int>()
        items.forEach { item ->
            finalHash.add(item.hashStruct().getHashed())
        }
        return StaticHash(CRC32CHasher.ofList(finalHash))

    }

    companion object : NetworkReadable<ContainerComponent> {
        override fun read(buffer: ByteBuf): ContainerComponent {
            return ContainerComponent(buffer.readList(ItemStack::read))
        }
    }
}