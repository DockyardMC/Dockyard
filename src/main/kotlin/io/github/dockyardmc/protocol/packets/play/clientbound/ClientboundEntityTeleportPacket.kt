package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeRotation
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.vectors.Vector3d

class ClientboundEntityTeleportPacket(entity: Entity, location: Location): ClientboundPacket() {

    constructor(entity: Entity) : this(entity, entity.location)

    init {
        buffer.writeVarInt(entity.id)
        location.toVector3d().write(buffer)
        Vector3d().write(buffer)
        buffer.writeRotation(location, false)
        buffer.writeInt(0x0000)
        buffer.writeBoolean(entity.isOnGround)
    }
}
