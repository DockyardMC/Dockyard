package io.github.dockyardmc.events

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.location.writeRotation
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundMoveVehiclePacket(var vehicle: Entity): ClientboundPacket() {

    init {
        data.writeLocation(vehicle.location)
        data.writeRotation(vehicle.location, false)
    }

}