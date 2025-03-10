package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.EntityMetadata
import io.github.dockyardmc.entity.writeMetadata
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetEntityMetadataPacket(entity: Entity, metadata: Collection<EntityMetadata>) : ClientboundPacket() {

    init {
        data.writeVarInt(entity.id)
        metadata.forEach {
            data.writeMetadata(it)
        }
        // array end byte
        data.writeByte(0xFF)
    }
}