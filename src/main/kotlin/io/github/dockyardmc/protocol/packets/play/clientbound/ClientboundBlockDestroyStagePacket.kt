package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundBlockDestroyStagePacket(val breaker: Entity, val location: Location, val destroyStage: Int): ClientboundPacket() {

    init {
        buffer.writeVarInt(breaker.id)
        buffer.writeLocation(location)
        buffer.writeByte(destroyStage.coerceIn(0, 9))
    }

}