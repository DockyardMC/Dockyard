package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeRotation
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.vectors.Vector3d
import io.github.dockyardmc.utils.vectors.writeVector3d

class ClientboundEntityTeleportPacket(entity: Entity, location: Location): ClientboundPacket() {

    constructor(entity: Entity) : this(entity, entity.location)

    init {
        data.writeVarInt(entity.entityId)
        data.writeVector3d(location.toVector3d())
        data.writeVector3d(Vector3d())
        data.writeRotation(location, false)
        data.writeInt(0x0000)
        data.writeBoolean(entity.isOnGround)
    }
}
