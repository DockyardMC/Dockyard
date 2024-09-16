package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@ClientboundPacketInfo(0x1F, ProtocolState.PLAY)
class ClientboundEntityEventPacket(entity: Entity, event: EntityEvent): ClientboundPacket() {

    init {
        data.writeInt(entity.entityId)
        data.writeByte(event.id)
    }

}

enum class EntityEvent(val id: Int) {
    ENTITY_DIE(3),
    WARDEN_ATTACK(4),
    PLAYER_ITEM_USE_FINISHED(9),
    PLAYER_ENABLE_DEBUG_SCREEN(23),
    PLAYER_DISABLE_DEBUG_SCREEN(22),
    PLAYER_SET_OP_PERMISSION_LEVEL_0(24),
    PLAYER_SET_OP_PERMISSION_LEVEL_1(25),
    PLAYER_SET_OP_PERMISSION_LEVEL_2(26),
    PLAYER_SET_OP_PERMISSION_LEVEL_3(27),
    PLAYER_SET_OP_PERMISSION_LEVEL_4(28),
    PLAYER_PLAY_TOTEM_ANIMATION(35),
    WARDEN_TENDRIL_SHAKING(61),
    WARDEN_SONIC_BOOM(62),
}