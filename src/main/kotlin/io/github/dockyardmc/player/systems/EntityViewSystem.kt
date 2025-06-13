package io.github.dockyardmc.player.systems

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player
import java.util.concurrent.locks.ReentrantLock

class EntityViewSystem(val player: Player) : TickablePlayerSystem {

    var visibleEntities: MutableList<Entity> = mutableListOf()
    val lock = ReentrantLock()

    fun clear() {
        synchronized(visibleEntities) {
            visibleEntities.toList().forEach { entity ->
                entity.removeViewer(player)
            }
            visibleEntities.clear()
        }
    }

    override fun tick() {
        if (lock.isLocked) return
        val entities = player.world.entities.toList().filter { it.autoViewable && it != player }

        val add = entities.filter { entity ->
            entity.location.distance(player.location) <= entity.viewDistanceBlocks
                    && !visibleEntities.contains(entity)
                    && entity.passesViewRules(player)
        }
        val remove = entities.filter { entity ->
            (entity.location.distance(player.location) > entity.viewDistanceBlocks || !entity.passesViewRules(player)) &&
                    visibleEntities.contains(entity)
        }

        add.forEach { entity ->
            entity.addViewer(player)
        }
        remove.forEach { entity ->
            entity.removeViewer(player)
        }
    }
}
