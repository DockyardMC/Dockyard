package io.github.dockyardmc.test

import cz.lukynka.Bindable
import de.metaphoriker.pathetic.api.pathing.filter.filters.PassablePathFilter
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.Ravager
import io.github.dockyardmc.entity.ai.AIManager
import io.github.dockyardmc.entity.ai.PlayAmbientNoiseAIGoal
import io.github.dockyardmc.entity.ai.RandomLookAroundAIGoal
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.WorldTickEvent
import io.github.dockyardmc.events.system.EventFilter
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.Navigator
import io.github.dockyardmc.pathfinding.Pathfinder
import io.github.dockyardmc.pathfinding.RequiredSizePathfindingFilter
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.registries.EntityType

class FloorOneRavager(location: Location) : Ravager(location) {
    override var health: Bindable<Float> = Bindable(1f)
    override var inventorySize: Int = 0
    override var type: EntityType = EntityTypes.RAVAGER

    val eventPool = EventPool().withFilter(EventFilter.containsWorld(world))

    val pathfinder = Pathfinder.createPathfinder {
        async(true)
        fallback(false)
    }
    val filters = listOf(PassablePathFilter(), RequiredSizePathfindingFilter(2, 2))
    val navigator = Navigator(this, 5, pathfinder, filters)

    val brain = AIManager(this)

    var target: Player? = null

    fun getPlayerTarget(): Player? {
        return target
    }

    init {
        brain.addGoal(PursuitPlayerAIGoal(this, 2, ::getPlayerTarget, navigator))

        brain.addGoal(RandomLookAroundAIGoal(this, 1))
        brain.addGoal(RandomWalkAroundGoal(this, 1, navigator))
        brain.addGoal(PlayAmbientNoiseAIGoal(this, 1, Sounds.ENTITY_RAVAGER_AMBIENT))

        eventPool.on<WorldTickEvent> {
            val playersInArea = world.players.filter { player -> player.location.distance(this.location) <= 15 && !player.isFlying.value }

            if(target != null && playersInArea.isEmpty()) {
                target = null
                return@on
            }

            val player = playersInArea.firstOrNull() ?: return@on
            if(player.isFlying.value) return@on
            target = player

        }
    }
}