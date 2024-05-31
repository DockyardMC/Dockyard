package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundEntityTeleportPacket(entity: Entity, location: Location): ClientboundPacket(0x6D) {

    init {
        data.writeVarInt(entity.entityId)
        data.writeLocation(location, true)
        data.writeBoolean(entity.isOnGround)
    }
}