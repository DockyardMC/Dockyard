package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import de.metaphoriker.pathetic.api.pathing.configuration.HeuristicWeights
import de.metaphoriker.pathetic.api.pathing.filter.filters.PassablePathFilter
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerDamageEntityEvent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.Navigator
import io.github.dockyardmc.pathfinding.Pathfinder
import io.github.dockyardmc.pathfinding.RequiredHeightPathfindingFilter
import io.github.dockyardmc.registry.DamageTypes
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.maths.randomFloat
import io.github.dockyardmc.maths.randomInt

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

    val navigator = Navigator(this, 5, pathfinder, listOf(RequiredHeightPathfindingFilter(2), PassablePathFilter()))

    init {

//        brain.addGoal(ZombieGroanAiGoal(this, 1))
//        brain.addGoal(RandomWalkAroundGoal(this, 1, navigator))

        eventPool.on<PlayerDamageEntityEvent> { event ->
            val entity = event.entity
            if (entity != this) return@on

            entity.world.playSound(Sounds.ENTITY_ZOMBIE_HURT, pitch = randomFloat(0.7f, 1.2f))
            entity.damage(1f, DamageTypes.GENERIC, event.player, event.entity)
            event.player.sendMessage("<red>${health.value}")
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
        super.dispose()
    }
}
