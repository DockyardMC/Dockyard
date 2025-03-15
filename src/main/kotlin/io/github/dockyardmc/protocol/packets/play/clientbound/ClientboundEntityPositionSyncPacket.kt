package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.vectors.Vector3d

class ClientboundEntityPositionSyncPacket(val entity: Entity, val location: Location, val delta: Vector3d, val isOnGround: Boolean): ClientboundPacket() {
    init {
        buffer.writeVarInt(entity.id)
        location.toVector3d().write(buffer)
        delta.write(buffer)
        buffer.writeFloat(location.yaw)
        buffer.writeFloat(location.pitch)
        buffer.writeBoolean(isOnGround)
    }
}