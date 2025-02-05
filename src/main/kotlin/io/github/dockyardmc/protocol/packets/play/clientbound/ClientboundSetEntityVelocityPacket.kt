package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.writeShortVector3

@WikiVGEntry("Set Entity Velocity")
@ClientboundPacketInfo(0x5A, ProtocolState.PLAY)
class ClientboundSetEntityVelocityPacket(entity: Entity, velocity: Vector3): ClientboundPacket() {

    init {
        data.writeVarInt(entity.id)
        data.writeShortVector3(velocity)
    }

}