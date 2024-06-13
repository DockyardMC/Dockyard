package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundEntityEffectPacket(
    val entity: Entity,
    val effectId: Int, //TODO Make list
    val amplifier: Int,
    val duration: Int,
    val flags: Int,
//    val hasFactorData: Boolean,
//    val factorCodec: NBT
): ClientboundPacket(0x72, ProtocolState.PLAY) {

    init {
        data.writeVarInt(entity.entityId)
        data.writeVarInt(effectId)
        data.writeByte(amplifier)
        data.writeVarInt(duration)
        data.writeByte(flags)
        data.writeBoolean(false) //TODO factor stuff
    }
}