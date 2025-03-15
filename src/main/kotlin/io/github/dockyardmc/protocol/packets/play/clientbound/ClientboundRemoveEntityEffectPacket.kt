package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.registry.registries.PotionEffect

class ClientboundRemoveEntityEffectPacket(entity: Entity, effect: PotionEffect) : ClientboundPacket() {

    constructor(entity: Entity, effect: AppliedPotionEffect) : this(entity, effect.effect)

    init {
        buffer.writeVarInt(entity.id)
        buffer.writeVarInt(effect.getProtocolId())
    }

}