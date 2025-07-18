package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readRegistryEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.CatVariant
import io.github.dockyardmc.registry.registries.CatVariantRegistry
import io.netty.buffer.ByteBuf

data class CatVariantComponent(val variant: CatVariant) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(variant.getProtocolId())
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofRegistryEntry(variant))
    }

    companion object : NetworkReadable<CatVariantComponent> {
        override fun read(buffer: ByteBuf): CatVariantComponent {
            return CatVariantComponent(buffer.readRegistryEntry(CatVariantRegistry))
        }
    }
}