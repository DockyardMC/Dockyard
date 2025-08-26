package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.ItemRarity
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

data class RarityComponent(val rarity: ItemRarity) : DataComponent(true) {

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofEnum(rarity))
    }

    companion object : NetworkReadable<RarityComponent> {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.enum(), RarityComponent::rarity,
            ::RarityComponent
        )

        override fun read(buffer: ByteBuf): RarityComponent {
            return STREAM_CODEC.read(buffer)
        }
    }
}