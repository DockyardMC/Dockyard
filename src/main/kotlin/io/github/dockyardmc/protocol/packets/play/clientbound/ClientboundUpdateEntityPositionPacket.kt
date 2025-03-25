package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.maths.getRelativeCoords

class ClientboundUpdateEntityPositionPacket(
    val entity: Entity,
    previousLocation: Location,
) : ClientboundPacket() {

    init {
        val current = entity.location

        buffer.writeVarInt(entity.id)
        buffer.writeShort(getRelativeCoords(current.x, previousLocation.x))
        buffer.writeShort(getRelativeCoords(current.y, previousLocation.y))
        buffer.writeShort(getRelativeCoords(current.z, previousLocation.z))
        buffer.writeBoolean(entity.isOnGround)
    }
}