package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.WolfVariant
import io.github.dockyardmc.registry.registries.WolfVariantRegistry
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class WolfVariantComponent(val variant: WolfVariant) : DynamicVariantComponent<WolfVariant>(variant, WolfVariantRegistry) {

    companion object : NetworkReadable<WolfVariantComponent> {
        override fun read(buffer: ByteBuf): WolfVariantComponent {
            return WolfVariantComponent(buffer.readVarInt().let { int -> WolfVariantRegistry.getByProtocolId(int) })
        }
    }

    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }
}