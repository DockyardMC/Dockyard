package io.github.dockyardmc.data.components

import io.github.dockyardmc.attributes.Modifier
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.HashList
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf

class AttributeModifiersComponent(val attributes: List<Modifier>) : DataComponent(true) {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(attributes, Modifier::write)
    }

    override fun hashStruct(): HashHolder {
        return HashList(attributes.map { attribute -> attribute.hashStruct() })
    }

    companion object : NetworkReadable<AttributeModifiersComponent> {

        override fun read(buffer: ByteBuf): AttributeModifiersComponent {
            return AttributeModifiersComponent(buffer.readList(Modifier::read))
        }
    }
}