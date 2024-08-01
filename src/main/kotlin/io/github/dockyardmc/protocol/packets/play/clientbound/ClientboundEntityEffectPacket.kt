package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.registry.PotionEffect

@WikiVGEntry("Entity Effect")
@ClientboundPacketInfo(0x76, ProtocolState.PLAY)
class ClientboundEntityEffectPacket(
    var entity: Entity,
    var effect: PotionEffect,
    var amplifier: Int,
    var duration: Int,
    var showParticles: Boolean = false,
    var showBlueBorder: Boolean = false,
    var showIconOnHud: Boolean = true,
): ClientboundPacket() {

    init {
        var flags = 0
        if(showBlueBorder) flags += 0x01
        if(showParticles) flags += 0x02
        if(showIconOnHud) flags += 0x04
        amplifier -= 1
        if(amplifier <= -1) amplifier = 0

        data.writeVarInt(entity.entityId)
        data.writeVarInt(effect.id)
        data.writeByte(amplifier)
        data.writeVarInt(duration)
        data.writeByte(flags)
    }
}