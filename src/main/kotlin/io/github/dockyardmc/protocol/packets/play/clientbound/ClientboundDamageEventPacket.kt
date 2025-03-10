package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.registries.DamageType

class ClientboundDamageEventPacket(entity: Entity, type: DamageType, attacker: Entity?, projectile: Entity?, location: Location? = null) : ClientboundPacket() {

    init {
        data.writeVarInt(entity.id)
        data.writeVarInt(type.getProtocolId())
        data.writeVarInt(if (attacker != null) attacker.id + 1 else 0)
        var sourceDirectId = 0
        if (projectile != null) sourceDirectId = projectile.id
        if (projectile != null && attacker != null) {
            sourceDirectId = attacker.id
        }
        data.writeVarInt(sourceDirectId)
        data.writeBoolean(location != null)
        if (location != null) {
            data.writeDouble(location.x)
            data.writeDouble(location.y)
            data.writeDouble(location.z)
        }
    }
}