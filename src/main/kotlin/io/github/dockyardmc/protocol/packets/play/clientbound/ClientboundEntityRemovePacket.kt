package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarIntArray
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Remove Entities")
class ClientboundEntityRemovePacket(entities: MutableList<Entity>): ClientboundPacket(0x42, ProtocolState.PLAY) {
    constructor(entity: Entity) : this(mutableListOf(entity))

    init {
        data.writeVarIntArray(entities.map { it.entityId })
    }
}