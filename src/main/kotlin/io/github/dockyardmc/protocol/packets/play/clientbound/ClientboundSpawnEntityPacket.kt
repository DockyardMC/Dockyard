package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.location.writeRotation
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.writeVelocity
import java.util.*

class ClientboundSpawnEntityPacket(
    entityId: Int,
    entityUUID: UUID,
    entityType: Int,
    location: Location,
    headYaw: Float,
    entityData: Int,
    velocity: Vector3
) : ClientboundPacket() {

    init {
        buffer.writeVarInt(entityId)
        buffer.writeUUID(entityUUID)
        buffer.writeVarInt(entityType)
        buffer.writeLocation(location)
        buffer.writeRotation(location, true)
        buffer.writeByte((headYaw * 256 / 360).toInt())
        buffer.writeVarInt(entityData)
        buffer.writeVelocity(velocity)
    }
}