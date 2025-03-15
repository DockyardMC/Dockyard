package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.vectors.Vector2f

class ClientboundUpdateEntityRotationPacket(
    entity: Entity,
    rotation: Vector2f,
) : ClientboundPacket() {

    init {
        buffer.writeVarInt(entity.id)
        buffer.writeByte((entity.location.yaw * 256 / 360).toInt())
        buffer.writeByte((entity.location.pitch * 256 / 360).toInt())
        buffer.writeBoolean(entity.isOnGround)
    }
}