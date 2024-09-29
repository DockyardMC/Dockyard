package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.registry.registries.PotionEffect

@WikiVGEntry("Remove Entity Effect")
@ClientboundPacketInfo(0x43, ProtocolState.PLAY)
class ClientboundRemoveEntityEffectPacket(entity: Entity, effect: PotionEffect): ClientboundPacket() {

    constructor(entity: Entity, effect: AppliedPotionEffect): this(entity, effect.effect)

    init {
        data.writeVarInt(entity.entityId)
        data.writeVarInt(effect.getProtocolId())
    }

}