package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.item.Enchantment
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.networktypes.readList
import io.github.dockyardmc.protocol.networktypes.writeList
import io.netty.buffer.ByteBuf

class EnchantmentsComponent(val list: List<Enchantment>): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(list, Enchantment::write)
    }

    companion object: NetworkReadable<EnchantmentsComponent> {
        override fun read(buffer: ByteBuf): EnchantmentsComponent {
            return EnchantmentsComponent(buffer.readList(Enchantment::read))
        }
    }
}