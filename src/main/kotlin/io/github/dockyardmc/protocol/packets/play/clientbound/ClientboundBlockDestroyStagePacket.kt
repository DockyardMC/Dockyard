package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.minMax

class ClientboundBlockDestroyStagePacket(val breaker: Entity, val location: Location, val destroyStage: Int): ClientboundPacket() {

    init {
        buffer.writeVarInt(breaker.id)
        buffer.writeLocation(location)
        buffer.writeByte(minMax(destroyStage, 0, 9))
    }

}