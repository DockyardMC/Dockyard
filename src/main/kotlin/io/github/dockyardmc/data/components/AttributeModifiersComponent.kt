package io.github.dockyardmc.data.components

import io.github.dockyardmc.attributes.Modifier
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.HashList
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class AttributeModifiersComponent(val attributes: List<Modifier>) : DataComponent(true) {

    override fun write(buffer: ByteBuf) {
        return NETWORK_CODEC.writeNetwork(buffer, this)
    }

    override fun hashStruct(): HashHolder {
        return HashList(attributes.map { attribute -> attribute.hashStruct() })
    }

    companion object : NetworkReadable<AttributeModifiersComponent> {
        val NETWORK_CODEC = Codec.of("attributes", Modifier.NETWORK_CODEC.list(), AttributeModifiersComponent::attributes, ::AttributeModifiersComponent)

        override fun read(buffer: ByteBuf): AttributeModifiersComponent {
            return NETWORK_CODEC.readNetwork(buffer)
        }
    }
}