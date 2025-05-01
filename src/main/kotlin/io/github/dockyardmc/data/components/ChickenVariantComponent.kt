package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.ChickenVariant
import io.github.dockyardmc.registry.registries.ChickenVariantRegistry
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class ChickenVariantComponent(val variant: ChickenVariant) : DynamicVariantComponent<ChickenVariant>(variant, ChickenVariantRegistry) {

    companion object : NetworkReadable<ChickenVariantComponent> {
        override fun read(buffer: ByteBuf): ChickenVariantComponent {
            return ChickenVariantComponent(buffer.readVarInt().let { int -> ChickenVariantRegistry.getByProtocolId(int) })
        }
    }

    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }
}