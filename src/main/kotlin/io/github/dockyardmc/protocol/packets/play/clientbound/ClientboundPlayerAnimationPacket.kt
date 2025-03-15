package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket


class ClientboundPlayerAnimationPacket(entity: Entity, animation: EntityAnimation) : ClientboundPacket() {

    init {
        buffer.writeVarInt(entity.id)
        buffer.writeByte(animation.ordinal)
    }

}

enum class EntityAnimation {
    SWING_MAIN_ARM,
    LEAVE_BED,
    SWING_OFFHAND,
    CRITICAL_EFFECT,
    MAGIC_CRITICAL_EFFECT
}