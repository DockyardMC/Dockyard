package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.waypoint.WaypointData

data class ClientboundTrackedWaypointPacket(val operation: Operation, val waypointData: WaypointData) : ClientboundPacket() {

    enum class Operation {
        TRACK,
        UNTRACK,
        UPDATE
    }

    init {
        buffer.writeEnum(operation)
        waypointData.write(buffer)
    }

}