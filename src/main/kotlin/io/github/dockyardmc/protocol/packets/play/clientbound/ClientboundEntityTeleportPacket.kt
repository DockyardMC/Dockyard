package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Teleport Entity")
@ClientboundPacketInfo(0x70, ProtocolState.PLAY)
class ClientboundEntityTeleportPacket(entity: Entity, location: Location): ClientboundPacket() {

    constructor(entity: Entity) : this(entity, entity.location)

    init {
        data.writeVarInt(entity.entityId)
        data.writeLocation(location, false)
        data.writeBoolean(entity.isOnGround)
    }
}