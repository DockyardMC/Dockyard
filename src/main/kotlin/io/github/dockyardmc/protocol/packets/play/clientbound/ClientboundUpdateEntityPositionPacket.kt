package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.utils.getRelativeCoords

@WikiVGEntry("Update Entity Position")
@ClientboundPacketInfo(0x2E, ProtocolState.PLAY)
class ClientboundUpdateEntityPositionPacket(
    val entity: Entity,
    previousLocation: Location,
): ClientboundPacket() {

    init {
        val current = entity.location

        data.writeVarInt(entity.entityId)
        data.writeShort(getRelativeCoords(current.x, previousLocation.x))
        data.writeShort(getRelativeCoords(current.y, previousLocation.y))
        data.writeShort(getRelativeCoords(current.z, previousLocation.z))
        data.writeBoolean(entity.isOnGround)
    }
}