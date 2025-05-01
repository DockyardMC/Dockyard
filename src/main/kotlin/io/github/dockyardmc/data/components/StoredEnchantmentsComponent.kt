package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.item.Enchantment
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class StoredEnchantmentsComponent(val enchantments: List<Enchantment>) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(enchantments, Enchantment::write)
    }

    companion object : NetworkReadable<StoredEnchantmentsComponent> {
        override fun read(buffer: ByteBuf): StoredEnchantmentsComponent {
            return StoredEnchantmentsComponent(buffer.readList(Enchantment::read))
        }
    }

}