package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.ItemRarity
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class RarityComponent(val rarity: ItemRarity) : DataComponent(true) {

    override fun write(buffer: ByteBuf) {
        CODEC.writeNetwork(buffer, this)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofEnum(rarity))
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