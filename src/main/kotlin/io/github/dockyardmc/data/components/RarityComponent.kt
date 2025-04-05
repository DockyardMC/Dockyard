package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.ItemRarity
import io.netty.buffer.ByteBuf

class RarityComponent(val rarity: ItemRarity) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(rarity)
    }

    companion object : NetworkReadable<RarityComponent> {
        override fun read(buffer: ByteBuf): RarityComponent {
            return RarityComponent(buffer.readEnum())
        }
    }
}