package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.vectors.Vector3d
import io.github.dockyardmc.utils.vectors.writeVector3d

class ClientboundEntityPositionSyncPacket(val entity: Entity, val location: Location, val delta: Vector3d, val isOnGround: Boolean): ClientboundPacket() {
    init {
        buffer.writeVarInt(entity.id)
        buffer.writeVector3d(location.toVector3d())
        buffer.writeVector3d(delta)
        buffer.writeFloat(location.yaw)
        buffer.writeFloat(location.pitch)
        buffer.writeBoolean(isOnGround)
    }
}