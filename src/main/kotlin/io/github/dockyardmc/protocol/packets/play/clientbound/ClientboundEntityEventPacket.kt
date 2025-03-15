package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundEntityEventPacket(entity: Entity, event: EntityEvent) : ClientboundPacket() {

    init {
        buffer.writeInt(entity.id)
        buffer.writeByte(event.id)
    }

}

enum class EntityEvent(val id: Int) {
    ENTITY_DIE(3),
    WARDEN_ATTACK(4),
    RAVAGER_ATTACK_ANIMATION(4),
    RAVAGER_STUNNED(39),
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