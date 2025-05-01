package io.github.dockyardmc.data.components

import io.github.dockyardmc.attributes.Modifier
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class AttributeModifiersComponent(val attributes: List<Modifier>) : DataComponent(false, true) {

    override fun getHashCodec(): Codec<out DataComponent> {
        return HASH_CODEC
    }

    override fun write(buffer: ByteBuf) {
        return NETWORK_CODEC.writeNetwork(buffer, this)
    }

    companion object : NetworkReadable<AttributeModifiersComponent> {
        val NETWORK_CODEC = Codec.of("attributes", Modifier.NETWORK_CODEC.list(), AttributeModifiersComponent::attributes, ::AttributeModifiersComponent)
        val HASH_CODEC = Codec.of("attributes", Modifier.HASH_CODEC.list(), AttributeModifiersComponent::attributes, ::AttributeModifiersComponent)

        override fun read(buffer: ByteBuf): AttributeModifiersComponent {
            return NETWORK_CODEC.readNetwork(buffer)
        }
    }
}