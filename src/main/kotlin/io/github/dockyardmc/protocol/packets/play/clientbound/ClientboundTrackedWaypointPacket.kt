package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.Waypoint

data class ClientboundTrackedWaypointPacket(val operation: Operation, val waypoint: Waypoint) : ClientboundPacket() {

    enum class Operation {
        TRACK,
        UNTRACT,
        UPDATE
    }

    init {
        buffer.writeEnum(operation)
        waypoint.write(buffer)
    }

}