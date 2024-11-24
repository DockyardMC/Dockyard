package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.getRelativeCoords

class ClientboundUpdateEntityPositionAndRotationPacket(
    val entity: Entity,
    previousLocation: Location,
): ClientboundPacket() {

    init {
        val current = entity.location

        data.writeVarInt(entity.entityId)
        data.writeShort(getRelativeCoords(current.x, previousLocation.x))
        data.writeShort(getRelativeCoords(current.y, previousLocation.y))
        data.writeShort(getRelativeCoords(current.z, previousLocation.z))
        data.writeByte((entity.location.yaw * 256 / 360).toInt())
        data.writeByte((entity.location.pitch * 256 / 360).toInt())
        data.writeBoolean(entity.isOnGround)
    }
}