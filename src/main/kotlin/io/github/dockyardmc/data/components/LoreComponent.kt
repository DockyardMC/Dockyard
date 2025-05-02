package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readList
import io.github.dockyardmc.extentions.readTextComponent
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class LoreComponent(val lore: List<Component>) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(lore, ByteBuf::writeTextComponent)
    }

    companion object : NetworkReadable<LoreComponent> {
        override fun read(buffer: ByteBuf): LoreComponent {
            return LoreComponent(buffer.readList(ByteBuf::readTextComponent))
        }
    }
}