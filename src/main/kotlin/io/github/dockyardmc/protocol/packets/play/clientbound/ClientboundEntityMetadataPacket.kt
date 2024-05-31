package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.writeMetadata
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundEntityMetadataPacket(entity: Entity): ClientboundPacket(0x56) {

    init {
        data.writeVarInt(entity.entityId)
        entity.metadata.forEach {
            data.writeByte(it.index.ordinal)
            data.writeMetadata(it)
        }
        // array end byte
        data.writeByte(0xff)
    }
}