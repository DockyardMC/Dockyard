package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.entities.writeMetadata
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundEntityMetadataPacket(entity: Entity): ClientboundPacket(0x56, ProtocolState.PLAY) {

    init {
        data.writeVarInt(entity.entityId)
        entity.metadata.values.forEach {
            data.writeByte(it.index.index)
            data.writeMetadata(it)
        }
        // array end byte
        data.writeByte(0xff)
    }
}