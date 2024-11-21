package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.registries.PotionEffect
import kotlin.experimental.or

class ClientboundEntityEffectPacket(
    var entity: Entity,
    var effect: PotionEffect,
    var amplifier: Int,
    var duration: Int,
    var showParticles: Boolean = false,
    var showBlueBorder: Boolean = false,
    var showIconOnHud: Boolean = true,
) : ClientboundPacket() {

    init {
        val flags: Byte = 0
        if (showBlueBorder) flags or 0x01
        if (showParticles) flags or 0x02
        if (showIconOnHud) flags or 0x04

        amplifier -= 1
        if (amplifier <= -1) amplifier = 0

        data.writeVarInt(entity.entityId)
        data.writeVarInt(effect.getProtocolId())
        data.writeVarInt(amplifier)
        data.writeVarInt(duration)
        data.writeByte(flags.toInt())
    }
}