package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundSetHeadYawPacket(entity: Entity): ClientboundPacket(0x46, ProtocolState.PLAY) {

    init {
        data.writeVarInt(entity.entityId)
        data.writeByte((entity.location.yaw * 256 / 360).toInt())
    }

}