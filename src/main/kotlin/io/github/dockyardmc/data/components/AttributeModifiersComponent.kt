package io.github.dockyardmc.data.components

import io.github.dockyardmc.attributes.AttributeModifier
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class AttributeModifiersComponent(val attributes: List<AttributeModifier>) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(attributes, AttributeModifier::write)
    }

    companion object : NetworkReadable<AttributeModifiersComponent> {
        override fun read(buffer: ByteBuf): AttributeModifiersComponent {
            return AttributeModifiersComponent(buffer.readList(AttributeModifier::read))
        }
    }
}