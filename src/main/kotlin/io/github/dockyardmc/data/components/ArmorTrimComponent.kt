package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readRegistryEntry
import io.github.dockyardmc.extentions.writeRegistryEntry
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.TrimMaterial
import io.github.dockyardmc.registry.registries.TrimMaterialRegistry
import io.github.dockyardmc.registry.registries.TrimPattern
import io.github.dockyardmc.registry.registries.TrimPatternRegistry
import io.netty.buffer.ByteBuf

class ArmorTrimComponent(val material: TrimMaterial, val pattern: TrimPattern) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeRegistryEntry(material)
        buffer.writeRegistryEntry(pattern)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("material", CRC32CHasher.ofRegistryEntry(material))
            static("pattern", CRC32CHasher.ofRegistryEntry(pattern))
        }
    }

    companion object : NetworkReadable<ArmorTrimComponent> {
        override fun read(buffer: ByteBuf): ArmorTrimComponent {
            return ArmorTrimComponent(
                buffer.readRegistryEntry(TrimMaterialRegistry),
                buffer.readRegistryEntry(TrimPatternRegistry),
            )
        }
    }
}