package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readRegistryEntry
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.ChickenVariant
import io.github.dockyardmc.registry.registries.ChickenVariantRegistry
import io.netty.buffer.ByteBuf

class ChickenVariantComponent(val variant: ChickenVariant) : DynamicVariantComponent<ChickenVariant>(variant, ChickenVariantRegistry) {

    companion object : NetworkReadable<ChickenVariantComponent> {
        override fun read(buffer: ByteBuf): ChickenVariantComponent {
            return ChickenVariantComponent(buffer.readRegistryEntry(ChickenVariantRegistry))
        }
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofRegistryEntry(variant))
    }
}