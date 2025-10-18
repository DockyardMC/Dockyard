package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetEntityMetadataPacket(entity: Entity, metadata: Collection<Metadata.MetadataDefinition.Value<*>>) : ClientboundPacket() {

    init {
        buffer.writeVarInt(entity.id)
        metadata.forEach { entry -> writeMetadataValue<Any?>(entry) }
        // array end byte
        buffer.writeByte(0xFF)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> writeMetadataValue(value: Metadata.MetadataDefinition.Value<*>) {
        val typedValue = value as Metadata.MetadataDefinition.Value<T>
        buffer.writeByte(value.parent.index)
        buffer.writeVarInt(typedValue.parent.type.index)
        typedValue.parent.type.streamCodec.write(buffer, typedValue.value)
    }
}