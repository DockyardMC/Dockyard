package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarIntArray
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundEntityRemovePacket(entities: MutableList<Entity>) : ClientboundPacket() {
    constructor(entity: Entity) : this(mutableListOf(entity))

    init {
        buffer.writeVarIntArray(entities.map { it.id })
    }
}