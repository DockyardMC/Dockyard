package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.netty.buffer.ByteBuf

abstract class DynamicVariantComponent<T : RegistryEntry>(internal val entry: T, val registry: DynamicRegistry) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(entry.getProtocolId())
    }

}