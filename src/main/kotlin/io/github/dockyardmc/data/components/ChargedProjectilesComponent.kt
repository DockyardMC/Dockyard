package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf

class ChargedProjectilesComponent(val projectiles: List<ItemStack>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(projectiles, ItemStack::write)
    }

    companion object : NetworkReadable<ChargedProjectilesComponent> {
        override fun read(buffer: ByteBuf): ChargedProjectilesComponent {
            return ChargedProjectilesComponent(buffer.readList(ItemStack::read))
        }
    }
}