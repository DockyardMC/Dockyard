package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Entity Effect")
@ClientboundPacketInfo(0x76, ProtocolState.PLAY)
class ClientboundEntityEffectPacket(
    entity: Entity,
    effectId: Int, //TODO Make list
    amplifier: Int,
    duration: Int,
    flags: Int,
): ClientboundPacket() {

    init {
        data.writeVarInt(entity.entityId)
        data.writeVarInt(effectId)
        data.writeByte(amplifier)
        data.writeVarInt(duration)
        data.writeByte(flags)
    }
}