package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.writeVelocity

class ClientboundSetEntityVelocityPacket(entity: Entity, velocity: Vector3d) : ClientboundPacket() {

    init {
        buffer.writeVarInt(entity.id)
        buffer.writeVelocity(velocity)
    }

}

