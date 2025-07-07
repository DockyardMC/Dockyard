package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import de.metaphoriker.pathetic.api.pathing.configuration.HeuristicWeights
import io.github.dockyardmc.entity.ai.test.SculkZombieBehaviourCoordinator
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerDamageEntityEvent
import io.github.dockyardmc.events.WorldTickEvent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.randomFloat
import io.github.dockyardmc.pathfinding.Pathfinder
import io.github.dockyardmc.protocol.types.EquipmentSlot
import io.github.dockyardmc.registry.DamageTypes
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.sounds.playSound
import kotlin.random.Random

class TestZombie(location: Location) : Entity(location) {
    override var type: EntityType = EntityTypes.ZOMBIE
    override val health: Bindable<Float> = Bindable(20f)
    override var inventorySize: Int = 0

    val eventPool = EventPool()
    val pathfinder = Pathfinder.createPathfinder {
        async(true)
        maxLength(25)
        maxIterations(256)
        heuristicWeights(HeuristicWeights.DIRECT_PATH_WEIGHTS)
    }

    val behaviourCoordinator = SculkZombieBehaviourCoordinator(this)

    init {
        eventPool.on<PlayerDamageEntityEvent> { event ->
            val entity = event.entity
            if (entity != this) return@on

            entity.world.playSound(Sounds.ENTITY_ZOMBIE_HURT, pitch = Random.randomFloat(0.7f, 1.2f))
            entity.damage(1f, DamageTypes.GENERIC, event.player, event.entity)
            event.player.sendMessage("<red>${health.value}")
        }

        eventPool.on<WorldTickEvent> {
            behaviourCoordinator.tick()
        }



//        eventPool.on<PlayerInteractWithEntityEvent> {
//            val entity = it.entity
//            val player = it.player
//            if (entity != this) return@on
//
//            player.sendMessage("<red>[NPC] Zombie: <white>grrrrr")
//            player.playSound(Sound("minecraft:entity.zombie.ambient", pitch = randomFloat(0.7f, 1.2f)))
//        }
    }

    // when entity is despawned
    override fun dispose() {
        eventPool.dispose() // automatically unregister all above events
        behaviourCoordinator.dispose()
        super.dispose()
    }
}
