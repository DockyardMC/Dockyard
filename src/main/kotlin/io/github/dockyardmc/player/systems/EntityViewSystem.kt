package io.github.dockyardmc.player.systems

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.addIfNotPresent
import io.github.dockyardmc.extentions.removeIfPresent
import io.github.dockyardmc.player.Player

class EntityViewSystem(val player: Player): TickablePlayerSystem {

    var visibleEntities: MutableList<Entity> = mutableListOf()

    fun clear() {
        synchronized(visibleEntities) {
            visibleEntities.toList().forEach { entity ->
                entity.removeViewer(player)
            }
            visibleEntities.clear()
        }
    }

    override fun tick() {
        val entities = player.world.entities.toList().filter { it.autoViewable && it != player }

        val add = entities.filter { it.location.distance(player.location) <= it.viewDistanceBlocks && !visibleEntities.contains(it) }
        val remove = entities.filter { it.location.distance(player.location) > it.viewDistanceBlocks && visibleEntities.contains(it) }

        add.forEach { entity -> entity.addViewer(player); visibleEntities.addIfNotPresent(entity) }
        remove.forEach { entity -> entity.removeViewer(player); visibleEntities.removeIfPresent(entity) }
    }
}