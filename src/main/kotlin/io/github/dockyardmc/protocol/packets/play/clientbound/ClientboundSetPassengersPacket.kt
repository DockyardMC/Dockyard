package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetPassengersPacket(val vehicle: Entity, val passengers: Collection<Entity>): ClientboundPacket() {

    constructor(vehicle: Entity, vararg passengers: Entity): this(vehicle, passengers.toList())
    constructor(vehicle: Entity, passenger: Entity): this(vehicle, listOf(passenger))

    init {
        data.writeVarInt(vehicle.entityId)
        data.writeVarInt(passengers.size)
        passengers.forEach {
            data.writeVarInt(it.entityId)
        }
    }
}