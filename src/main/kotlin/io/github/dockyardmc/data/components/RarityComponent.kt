package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.extentions.writeVarIntEnum
import io.github.dockyardmc.item.ItemRarity
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class RarityComponent(val rarity: ItemRarity): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarIntEnum(rarity)
    }

    companion object: NetworkReadable<RarityComponent> {
        override fun read(buffer: ByteBuf): RarityComponent {
            return RarityComponent(buffer.readVarIntEnum())
        }
    }
}