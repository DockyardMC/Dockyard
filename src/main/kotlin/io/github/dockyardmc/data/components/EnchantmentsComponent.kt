package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.item.Enchantment
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf

class EnchantmentsComponent(val list: List<Enchantment>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(list, Enchantment::write)
    }

    //TODO(Enchantments)
    override fun hashStruct(): HashHolder {
        return unsupported(this::class)
    }

    companion object : NetworkReadable<EnchantmentsComponent> {
        override fun read(buffer: ByteBuf): EnchantmentsComponent {
            return EnchantmentsComponent(buffer.readList(Enchantment::read))
        }
    }
}