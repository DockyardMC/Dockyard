package io.github.dockyardmc.player.systems

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player

class EntityViewSystem(val player: Player): TickablePlayerSystem {

    var visibleEntities: MutableList<Entity> = mutableListOf()

    override fun tick() {
        val entities = player.world.entities.toList().filter { it.autoViewable && it != player }

        val add = entities.filter { it.location.distance(player.location) <= it.renderDistanceBlocks && !visibleEntities.contains(it) }
        val remove = entities.filter { it.location.distance(player.location) > it.renderDistanceBlocks && visibleEntities.contains(it) }

        add.forEach { it.addViewer(player) }
        remove.forEach { it.removeViewer(player) }

    }
}