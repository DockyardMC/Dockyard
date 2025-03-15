package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetHeadYawPacket(entity: Entity, location: Location = entity.location) : ClientboundPacket() {

    init {
        buffer.writeVarInt(entity.id)
        buffer.writeByte(((location.yaw % 360) * 256 / 360).toInt())
    }
}