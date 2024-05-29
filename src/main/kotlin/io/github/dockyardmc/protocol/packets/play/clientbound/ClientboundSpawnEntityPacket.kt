package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.Velocity
import io.github.dockyardmc.utils.writeVelocity
import java.util.UUID

class ClientboundSpawnEntityPacket(
    entityId: Int,
    entityUUID: UUID,
    entityType: Int,
    location: Location,
    headYaw: Float,
    entityData: Int,
    velocity: Velocity
): ClientboundPacket(0x01) {

    init {
        data.writeVarInt(entityId)
        data.writeUUID(entityUUID)
        data.writeVarInt(entityType)
        data.writeLocation(location, true)
        data.writeByte((headYaw  * 256 / 360).toInt())
        data.writeVarInt(entityData)
        data.writeVelocity(velocity)
    }
}