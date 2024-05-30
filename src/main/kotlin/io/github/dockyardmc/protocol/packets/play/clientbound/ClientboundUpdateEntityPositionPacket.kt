package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundUpdateEntityPositionPacket(val entity: Entity, previousLocation: Location): ClientboundPacket(0x2C) {

    init {
        val current = entity.location

        data.writeVarInt(entity.entityId)
        data.writeShort(getRelative(current.x, previousLocation.x))
        data.writeShort(getRelative(current.y, previousLocation.y))
        data.writeShort(getRelative(current.z, previousLocation.z))
        val isOnGround = if(entity is Player) entity.isOnGround else true
        data.writeBoolean(isOnGround)
    }

    private fun getRelative(current: Double, previous: Double): Int {
        return ((current * 32 - previous * 32) * 128).toInt()
    }

}