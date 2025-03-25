package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import de.metaphoriker.pathetic.api.pathing.configuration.HeuristicWeights
import de.metaphoriker.pathetic.api.pathing.filter.filters.PassablePathFilter
import io.github.dockyardmc.entity.ai.AIGoal
import io.github.dockyardmc.entity.ai.AIManager
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
    override var health: Bindable<Float> = Bindable(20f)
    override var inventorySize: Int = 0

    val eventPool = EventPool()
    val brain = AIManager(this)
    val pathfinder = Pathfinder.createPathfinder {
        async(true)
        maxLength(25)
        maxIterations(256)
        heuristicWeights(HeuristicWeights.DIRECT_PATH_WEIGHTS)
    }

    val navigator = Navigator(this, 5, pathfinder, listOf(RequiredHeightPathfindingFilter(2), PassablePathFilter()))

    init {

        brain.addGoal(ZombieLookAroundAIGoal(this, 1))
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


class ZombieLookAroundAIGoal(override var entity: Entity, override var priority: Int) : AIGoal() {

    val zombie = entity as TestZombie
    val chancePerTick = 50

    var lookingAroundTime: Int = 0

    override fun startCondition(): Boolean {
        return randomInt(chancePerTick, 100) == chancePerTick

    }

    override fun start() {
        lookingAroundTime = 10
        zombie.teleport(zombie.location.clone().apply { pitch = 0f; yaw += randomFloat(-90f, 90f) })
    }

    override fun end() {
        cooldown = randomInt(3, 5) * 20
    }

    override fun endCondition(): Boolean = lookingAroundTime <= 0

    override fun tick() {
        lookingAroundTime--
    }
}

class ZombieGroanAiGoal(override var entity: Entity, override var priority: Int) : AIGoal() {

    val zombie = entity as TestZombie
    val chancePerTick = 50

    override fun startCondition(): Boolean {
        return randomInt(chancePerTick, 100) == chancePerTick
    }

    override fun start() {
        zombie.playSoundToViewers(Sound("minecraft:entity.zombie.ambient", pitch = randomFloat(0.8f, 1.2f)))
    }

    override fun end() {
        cooldown = randomInt(1, 2) * 20
    }

    override fun endCondition(): Boolean = true

    override fun tick() {}
}