package io.github.dockyardmc.entity.handlers

import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.entity.ItemDropEntity
import io.github.dockyardmc.events.EntityPickupItemEvent
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPickupItemPacket

class EntityItemPickupHandler(override val entity: Entity) : TickableEntityHandler {

    override fun tick() {
        synchronized(entity) {
            val world = entity.world
            val location = entity.location

            if(!ConfigManager.config.implementationConfig.itemDroppingAndPickup) return
            val drops = world.entities.filterIsInstance<ItemDropEntity>()
            if (entity.inventorySize <= 0) return
            drops.toList().forEach { drop ->
                if (entity is Player && !drop.viewers.contains(entity)) return@forEach
                if (!drop.canBePickedUp) return@forEach
                if (drop.location.distance(location) > drop.pickupDistance) return@forEach

                val itemStack = drop.itemStack.value

                val eventContext = Event.Context(
                    setOf(),
                    setOf(drop, entity),
                    setOf(entity.world),
                    setOf(entity.location, drop.location)
                )
                val event = EntityPickupItemEvent(entity, drop, eventContext)
                Events.dispatch(event)
                if (event.cancelled) return@forEach

                if (entity.canPickupItem(drop, itemStack)) {
                    val mutualViewers = drop.viewers.filter { entity.viewers.contains(it) }
                    if (drop.pickupAnimation) {
                        val packet = ClientboundPickupItemPacket(drop, entity, itemStack)
                        mutualViewers.sendPacket(packet)
                        if (entity is Player) entity.sendPacket(packet)
                    }
                    drop.world.despawnEntity(drop)
                    return@forEach
                }
            }
        }
    }
}
