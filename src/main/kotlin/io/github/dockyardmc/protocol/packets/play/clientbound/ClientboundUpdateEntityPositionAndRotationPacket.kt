package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.utils.MathUtils

class ClientboundUpdateEntityPositionAndRotationPacket(val entity: Entity, previousLocation: Location): ClientboundPacket(0x2D, ProtocolState.PLAY) {

    init {
        val current = entity.location

        data.writeVarInt(entity.entityId)
        data.writeShort(MathUtils.getRelativeCoords(current.x, previousLocation.x))
        data.writeShort(MathUtils.getRelativeCoords(current.y, previousLocation.y))
        data.writeShort(MathUtils.getRelativeCoords(current.z, previousLocation.z))
        data.writeByte((entity.location.yaw * 256 / 360).toInt())
        data.writeByte((entity.location.pitch * 256 / 360).toInt())
        data.writeBoolean(entity.isOnGround)
    }
}