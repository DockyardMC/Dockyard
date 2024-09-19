package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.writeVelocity
import java.util.UUID

@WikiVGEntry("Spawn Entity")
@ClientboundPacketInfo(0x01, ProtocolState.PLAY)
class ClientboundSpawnEntityPacket(
    entityId: Int,
    entityUUID: UUID,
    entityType: Int,
    location: Location,
    headYaw: Float,
    entityData: Int,
    velocity: Vector3
): ClientboundPacket() {

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