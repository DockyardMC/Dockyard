package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarIntArray
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundRemoveEntitiesPacket(entities: MutableList<Entity>): ClientboundPacket(0x40) {
    constructor(entity: Entity) : this(mutableListOf(entity))

    init {
        data.writeVarIntArray(entities.map { it.entityId })
    }
}