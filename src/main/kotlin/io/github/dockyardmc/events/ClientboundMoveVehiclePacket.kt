package io.github.dockyardmc.events

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.location.writeRotation
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundMoveVehiclePacket(var vehicle: Entity): ClientboundPacket() {

    init {
        buffer.writeLocation(vehicle.location)
        buffer.writeRotation(vehicle.location, false)
    }

}