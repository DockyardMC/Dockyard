package io.github.dockyardmc.entity.handlers

import cz.lukynka.BindableList
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.events.EntityDismountVehicleEvent
import io.github.dockyardmc.events.EntityRideVehicleEvent
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetPassengersPacket

class EntityVehicleHandler(override val entity: Entity) : EntityHandler {

    fun handle(passengers: BindableList<Entity>) {

        passengers.itemAdded {

            val contextPlayers = mutableSetOf<Player>()
            if (entity is Player) contextPlayers.add(entity)
            if (it.item is Player) contextPlayers.add(it.item as Player)

            val event = EntityRideVehicleEvent(
                entity, it.item, Event.Context(
                    contextPlayers,
                    setOf(entity, it.item),
                    setOf(entity.world),
                    setOf(entity.location),
                )
            )

            Events.dispatch(event)
            if (event.cancelled) {
                passengers.remove(it.item)
                return@itemAdded
            }

            it.item.vehicle = entity
            val packet = ClientboundSetPassengersPacket(entity, passengers.values)
            entity.sendPacketToViewers(packet)
        }


        passengers.itemRemoved {
            val contextPlayers = mutableSetOf<Player>()
            if(entity is Player) contextPlayers.add(entity)
            if(it.item is Player) contextPlayers.add(it.item as Player)

            val event = EntityDismountVehicleEvent(entity, it.item, Event.Context(
                contextPlayers,
                setOf(entity, it.item),
                setOf(entity.world),
                setOf(entity.location),
            ))

            Events.dispatch(event)
            if(event.cancelled) {
                passengers.remove(it.item)
                return@itemRemoved
            }

            it.item.vehicle = null
            val packet = ClientboundSetPassengersPacket(entity, passengers.values)
            entity.sendPacketToViewers(packet)
        }
    }
}