package io.github.dockyardmc.entity

import cz.lukynka.prettylog.log
import io.github.dockyardmc.events.EntitySpawnEvent
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.utils.getEntityEventContext
import io.github.dockyardmc.world.World
import java.util.concurrent.atomic.AtomicInteger

object EntityManager {

    var entityIdCounter = AtomicInteger()
    private val innerEntities: MutableMap<Int, Entity> = mutableMapOf()

    val entities get() = innerEntities.values

    fun addPlayer(player: Player) {
        synchronized(entities) {
            innerEntities[player.id] = player
        }
    }

    fun removePlayer(player: Player) {
        synchronized(entities) {
            innerEntities.remove(player.id)
        }
    }

    fun getByIdOrNull(id: Int): Entity? {
        return innerEntities[id]
    }

    fun getById(id: Int): Entity {
        return innerEntities[id] ?: throw NoSuchElementException("No entity with id $id exists!")
    }

    fun spawnEntity(entity: Entity): Entity {
        synchronized(entities) {
            innerEntities[entity.id] = entity
            entity.world.addEntity(entity)
        }

        val event = EntitySpawnEvent(entity, entity.world, getEntityEventContext(entity))
        Events.dispatch(event)

        if(event.cancelled) {
            entity.world.despawnEntity(entity)
        }

        return entity
    }

    fun despawnEntity(entity: Entity) {
        if(entity is Player) return
        synchronized(entities) {
            innerEntities.remove(entity.id)
            entity.world.removeEntity(entity)
        }
    }

    fun World.spawnEntity(entity: Entity): Entity {
        return this@EntityManager.spawnEntity(entity)
    }

    fun World.despawnEntity(entity: Entity) {
        if(entity is Player) return
        entity.dispose()
    }
}