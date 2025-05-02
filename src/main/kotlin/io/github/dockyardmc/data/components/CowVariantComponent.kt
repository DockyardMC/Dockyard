package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.CowVariant
import io.github.dockyardmc.registry.registries.CowVariantRegistry
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class CowVariantComponent(val variant: CowVariant) : DynamicVariantComponent<CowVariant>(variant, CowVariantRegistry) {

    companion object : NetworkReadable<CowVariantComponent> {
        override fun read(buffer: ByteBuf): CowVariantComponent {
            return CowVariantComponent(buffer.readVarInt().let { int -> CowVariantRegistry.getByProtocolId(int) })
        }
    }

    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }
}