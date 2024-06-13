package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.utils.Vector2

class ClientboundUpdateEntityRotationPacket(entity: Entity, rotation: Vector2): ClientboundPacket(0x2E, ProtocolState.PLAY) {

    init {
        data.writeVarInt(entity.entityId)
        data.writeByte((entity.location.yaw * 256 / 360).toInt())
        data.writeByte((entity.location.pitch * 256 / 360).toInt())
        data.writeBoolean(entity.isOnGround)
    }
}