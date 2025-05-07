package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.FrogVariant
import io.github.dockyardmc.registry.registries.FrogVariantRegistry
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class FrogVariantComponent(val variant: FrogVariant) : DynamicVariantComponent<FrogVariant>(variant, FrogVariantRegistry) {

    companion object : NetworkReadable<FrogVariantComponent> {
        override fun read(buffer: ByteBuf): FrogVariantComponent {
            return FrogVariantComponent(buffer.readVarInt().let { int -> FrogVariantRegistry.getByProtocolId(int) })
        }
    }
}