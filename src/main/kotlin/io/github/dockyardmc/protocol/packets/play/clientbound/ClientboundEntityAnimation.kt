package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundEntityAnimation(entity: Entity, animation: EntityAnimation): ClientboundPacket(0x03) {

    init {
        data.writeVarInt(entity.entityId)
        data.writeByte(animation.ordinal)
    }

}

enum class EntityAnimation {
    SWING_MAIN_ARM,
    LEAVE_BED,
    SWING_OFFHAND,
    CRITICAL_EFFECT,
    MAGIC_CRITICAL_EFFECT
}