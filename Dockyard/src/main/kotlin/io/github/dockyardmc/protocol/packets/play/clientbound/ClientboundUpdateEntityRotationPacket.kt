package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.utils.vectors.Vector2f

@WikiVGEntry("Update Entity Rotation")
@ClientboundPacketInfo(0x30, ProtocolState.PLAY)
class ClientboundUpdateEntityRotationPacket(
    entity: Entity,
    rotation: Vector2f,
): ClientboundPacket() {

    init {
        data.writeVarInt(entity.entityId)
        data.writeByte((entity.location.yaw * 256 / 360).toInt())
        data.writeByte((entity.location.pitch * 256 / 360).toInt())
        data.writeBoolean(entity.isOnGround)
    }
}