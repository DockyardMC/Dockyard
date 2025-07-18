package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.WolfSoundVariant
import io.github.dockyardmc.registry.registries.WolfSoundVariantRegistry
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class WolfSoundVariantComponent(val variant: WolfSoundVariant) : DynamicVariantComponent<WolfSoundVariant>(variant, WolfSoundVariantRegistry) {

    companion object : NetworkReadable<WolfSoundVariantComponent> {
        override fun read(buffer: ByteBuf): WolfSoundVariantComponent {
            return WolfSoundVariantComponent(buffer.readVarInt().let { int -> WolfSoundVariantRegistry.getByProtocolId(int) })
        }
    }
}