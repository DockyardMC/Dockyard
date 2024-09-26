package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.registry.registries.DamageType

@WikiVGEntry("Damage Event")
@ClientboundPacketInfo(0x1A, ProtocolState.PLAY)
class ClientboundDamageEventPacket(entity: Entity, type: DamageType, attacker: Entity?, projectile: Entity?, location: Location? = null): ClientboundPacket() {

    init {
        data.writeVarInt(entity.entityId)
        data.writeVarInt(type.protocolId)
        data.writeVarInt(if(attacker != null) attacker.entityId +1 else 0)
        var sourceDirectId = 0
        if(projectile != null) sourceDirectId = projectile.entityId
        if(projectile != null && attacker != null) {
            sourceDirectId = attacker.entityId
        }
        data.writeVarInt(sourceDirectId)
        data.writeBoolean(location != null)
        if(location != null) {
            data.writeDouble(location.x)
            data.writeDouble(location.y)
            data.writeDouble(location.z)
        }
    }
}