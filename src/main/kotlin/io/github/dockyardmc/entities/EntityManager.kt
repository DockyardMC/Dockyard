package io.github.dockyardmc.entities

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.world.World
import java.util.concurrent.atomic.AtomicInteger

object EntityManager {

    var entityIdCounter = AtomicInteger()
    val entities: MutableList<Entity> = mutableListOf()

    fun World.spawnEntity(entity: Entity) {
        this@EntityManager.entities.add(entity)
        PlayerManager.players.forEach { loopPlayer ->
            entity.addViewer(loopPlayer)
        }
    }

    fun World.despawnEntity(entity: Entity) {
        this@EntityManager.entities.add(entity)
        PlayerManager.players.forEach {
            entity.removeViewer(it, false)
        }
    }
}