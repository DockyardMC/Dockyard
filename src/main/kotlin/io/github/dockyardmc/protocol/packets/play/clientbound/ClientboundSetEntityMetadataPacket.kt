package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.writeMetadata
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetEntityMetadataPacket(entity: Entity, metadata: Collection<EntityMetadata>) : ClientboundPacket() {

    init {
        buffer.writeVarInt(entity.id)
        metadata.forEach {
            buffer.writeMetadata(it)
        }
        // array end byte
        buffer.writeByte(0xFF)
    }
}