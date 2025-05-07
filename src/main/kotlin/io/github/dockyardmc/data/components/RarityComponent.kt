package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.ItemRarity
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class RarityComponent(val rarity: ItemRarity) : DataComponent(true) {

    override fun write(buffer: ByteBuf) {
        CODEC.writeNetwork(buffer, this)
    }

    companion object : NetworkReadable<RarityComponent> {
        val CODEC = Codec.of(
            "rarity", Codec.enum<ItemRarity>(), RarityComponent::rarity,
            ::RarityComponent
        )

        override fun read(buffer: ByteBuf): RarityComponent {
            return CODEC.readNetwork(buffer)
        }
    }
}