package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.TrimMaterial
import io.github.dockyardmc.registry.registries.TrimMaterialRegistry
import io.github.dockyardmc.registry.registries.TrimPattern
import io.github.dockyardmc.registry.registries.TrimPatternRegistry
import io.netty.buffer.ByteBuf

class ArmorTrimComponent(val material: TrimMaterial, val pattern: TrimPattern) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(material.getProtocolId())
        buffer.writeVarInt(pattern.getProtocolId())
    }

    companion object : NetworkReadable<ArmorTrimComponent> {
        override fun read(buffer: ByteBuf): ArmorTrimComponent {
            return ArmorTrimComponent(
                buffer.readVarInt().let { int -> TrimMaterialRegistry.getByProtocolId(int) },
                buffer.readVarInt().let { int -> TrimPatternRegistry.getByProtocolId(int) },
            )
        }
    }
}