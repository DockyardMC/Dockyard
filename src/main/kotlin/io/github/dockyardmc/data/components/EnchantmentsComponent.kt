package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.item.Enchantment
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class EnchantmentsComponent(val list: List<Enchantment>): DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(list, Enchantment::write)
    }

    companion object: NetworkReadable<EnchantmentsComponent> {
        override fun read(buffer: ByteBuf): EnchantmentsComponent {
            return EnchantmentsComponent(buffer.readList(Enchantment::read))
        }
    }
}