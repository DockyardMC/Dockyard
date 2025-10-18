package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeByte
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.location.writeRotation
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.types.LpVector3d
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.utils.writeVelocity
import java.util.*

data class ClientboundSpawnEntityPacket(
    val entityId: Int,
    val entityUUID: UUID,
    val entityType: EntityType,
    val location: Location,
    val headYaw: Float,
    val entityData: Int,
    val velocity: Vector3d
) : ClientboundPacket() {

    init {
        buffer.writeVarInt(entityId)
        buffer.writeUUID(entityUUID)
        buffer.writeVarInt(entityType.getProtocolId())
        buffer.writeLocation(location)

        LpVector3d.STREAM_CODEC.write(buffer, LpVector3d(velocity))

        buffer.writeByte((location.pitch * 256f / 360f).toInt())
        buffer.writeByte((location.yaw * 256f / 360f).toInt())
        buffer.writeByte((headYaw * 256f / 360).toInt())
        buffer.writeVarInt(entityData)
    }
}