package io.github.dockyardmc.plugins.bundled.extras.items

import io.github.dockyardmc.entities.EntityManager.despawnEntity
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.entities.Pig
import io.github.dockyardmc.entities.Sheep
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerBlockRightClickEvent
import io.github.dockyardmc.events.PlayerDamageEntityEvent
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.Vector3f

class SpawnEggImplementation {

    init {
        Events.on<PlayerBlockRightClickEvent> {
            val player = it.player
            val heldItem = player.getHeldItem(PlayerHand.MAIN_HAND)

            if(!heldItem.material.identifier.contains("spawn_egg")) return@on

            val spawnLoc = it.location.apply { y += 1 }

            val entity = when(heldItem.material) {
                Items.PIG_SPAWN_EGG -> Pig(location = spawnLoc, world = player.world)
                Items.SHEEP_SPAWN_EGG -> Sheep(location = spawnLoc, world = player.world)
                else -> return@on
            }

            player.world.spawnEntity(entity)
        }

        Events.on<PlayerDamageEntityEvent> {
            val player = it.player
            val heldItem = player.getHeldItem(PlayerHand.MAIN_HAND)
            if(heldItem.material != Items.DEBUG_STICK) return@on
            if(!player.isSneaking) return@on
            if(it.entity.type == EntityTypes.PLAYER) return@on

            player.world.despawnEntity(it.entity)
            player.playSound("entity.mooshroom.convert", player.location, 0.2f, 2f)
            player.spawnParticle(it.entity.location, Particles.CLOUD, Vector3f(0.2f), count = 10, speed = 0.3f)
            player.spawnParticle(it.entity.location, Particles.WAX_OFF, Vector3f(0.5f), count = 10)
        }
    }
}