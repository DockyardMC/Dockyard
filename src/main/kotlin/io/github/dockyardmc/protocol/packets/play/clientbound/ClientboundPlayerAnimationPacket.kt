package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState


@WikiVGEntry("Entity Animation")
@ClientboundPacketInfo(0x03, ProtocolState.PLAY)
class ClientboundPlayerAnimationPacket(entity: Entity, animation: EntityAnimation): ClientboundPacket() {

    init {
        data.writeVarInt(entity.id)
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