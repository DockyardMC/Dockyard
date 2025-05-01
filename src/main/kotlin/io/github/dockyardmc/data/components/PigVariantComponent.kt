package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.PigVariant
import io.github.dockyardmc.registry.registries.PigVariantRegistry
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class PigVariantComponent(val variant: PigVariant) : DynamicVariantComponent<PigVariant>(variant, PigVariantRegistry) {

    companion object : NetworkReadable<PigVariantComponent> {
        override fun read(buffer: ByteBuf): PigVariantComponent {
            return PigVariantComponent(buffer.readVarInt().let { int -> PigVariantRegistry.getByProtocolId(int) })
        }
    }

    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }
}