package io.github.dockyardmc.entities

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.World
import java.util.concurrent.atomic.AtomicInteger

object EntityManager {

    var entityIdCounter = AtomicInteger()
    private val innerEntities: MutableList<Entity> = mutableListOf()

    val entities get() = innerEntities.toList()

    fun addPlayer(player: Player) {
        synchronized(entities) {
            innerEntities.add(player)
        }
    }

    fun spawnEntity(entity: Entity): Entity {
        synchronized(entities) {
            innerEntities.add(entity)
            entity.world.addEntity(entity)
        }

        return entity
    }

    fun despawnEntity(entity: Entity) {
        synchronized(entities) {
            innerEntities.remove(entity)
            entity.world.removeEntity(entity)
        }
    }

    fun World.spawnEntity(entity: Entity): Entity {
        return this@EntityManager.spawnEntity(entity)
    }

    fun World.despawnEntity(entity: Entity) {
        entity.dispose()
    }

    init {
        Events.on<ServerTickEvent> { event ->
            innerEntities.toList().forEach { if(it.tickable) it.tick(event.serverTicks) }
        }
    }
}