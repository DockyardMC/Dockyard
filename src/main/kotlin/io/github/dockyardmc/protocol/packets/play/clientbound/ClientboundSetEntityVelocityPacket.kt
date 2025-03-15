package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.vectors.Vector3

class ClientboundSetEntityVelocityPacket(entity: Entity, velocity: Vector3) : ClientboundPacket() {

    init {
        buffer.writeVarInt(entity.id)
        velocity.writeAsShorts(buffer)
    }

}