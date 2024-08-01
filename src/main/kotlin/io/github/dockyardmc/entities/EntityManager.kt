package io.github.dockyardmc.entities

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.world.World
import java.util.concurrent.atomic.AtomicInteger

object EntityManager {

    var entityIdCounter = AtomicInteger()
    val entities: MutableList<Entity> = mutableListOf()

    //TODO Add way to have fully client-side entities

    fun World.spawnEntity(entity: Entity) {
        this@EntityManager.entities.add(entity)
        this.entities.add(entity)
        PlayerManager.players.forEach { loopPlayer ->
            entity.addViewer(loopPlayer)
        }
    }

    fun World.despawnEntity(entity: Entity) {
        this@EntityManager.entities.add(entity)
        PlayerManager.players.forEach {
            entity.removeViewer(it, false)
        }
//        this.entities.remove(entity)
    }

    init {
        Events.on<ServerTickEvent> {
            entities.forEach { if(it.tickable) it.tick() }
        }
    }
}