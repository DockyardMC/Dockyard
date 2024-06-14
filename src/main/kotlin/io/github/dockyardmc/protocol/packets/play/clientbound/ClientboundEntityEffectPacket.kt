package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Entity Effect")
class ClientboundEntityEffectPacket(
    entity: Entity,
    effectId: Int, //TODO Make list
    amplifier: Int,
    duration: Int,
    flags: Int,
): ClientboundPacket(0x76, ProtocolState.PLAY) {

    init {
        data.writeVarInt(entity.entityId)
        data.writeVarInt(effectId)
        data.writeByte(amplifier)
        data.writeVarInt(duration)
        data.writeByte(flags)
        data.writeBoolean(false) //TODO factor stuff
    }
}