package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarIntArray
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Remove Entities")
@ClientboundPacketInfo(0x42, ProtocolState.PLAY)
class ClientboundEntityRemovePacket(entities: MutableList<Entity>): ClientboundPacket() {
    constructor(entity: Entity) : this(mutableListOf(entity))

    init {
        data.writeVarIntArray(entities.map { it.id })
    }
}