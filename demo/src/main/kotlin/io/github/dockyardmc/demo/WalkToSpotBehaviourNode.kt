package io.github.dockyardmc.demo

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ai.EntityBehaviourCoordinator
import io.github.dockyardmc.entity.ai.EntityBehaviourNode
import io.github.dockyardmc.entity.ai.EntityBehaviourResult
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard.toPathPosition
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.utils.debug

class WalkToSpotBehaviourNode(val coordinator: EntityBehaviourCoordinator, val destination: Location) : EntityBehaviourNode() {

    override val interruptible: Boolean = true
    var failedTimes: Int = 0
    var alreadyFinished = false

    override fun getScorer(entity: Entity): Float {
        return 1f
    }

    override fun onStart(entity: Entity) {
        var foundPath = false

        coordinator.navigator.navigationCompleteDispatcher.subscribe {
            getBehaviourFuture().complete(EntityBehaviourResult.SUCCESS)
            alreadyFinished = true
        }
        coordinator.navigator.navigationNodeStepDispatcher.subscribe {
            entity.playSoundToViewers(Sound(Sounds.ENTITY_COPPER_GOLEM_STEP))
        }

        val start = entity.location.getBlockLocation().subtract(0, 1, 0).toPathPosition()
        val end = destination.toPathPosition()

        coordinator.navigator.pathfinder.findPath(start, end, coordinator.navigator.filters).thenAccept { result ->
            if (!result.successful()) {
                debug("<red>failed pathfinding", true)
                return@thenAccept
            }
            if (foundPath) return@thenAccept

            foundPath = true
            debug("<lime>Found path", true)
            coordinator.navigator.cancelNavigating()
            coordinator.navigator.updatePathfindingPath(destination)
        }
    }

    override fun onBackstageTick(tick: Int) {
    }

    override fun onFrontstageTick(tick: Int) {
    }

    override fun onGeneralTick(tick: Int) {
    }

    override fun onStop(entity: Entity, interrupted: Boolean) {
        coordinator.navigator.cancelNavigating()
        coordinator.navigator.navigationNodeStepDispatcher.dispose()
        coordinator.navigator.navigationCompleteDispatcher.dispose()
        coordinator.navigator.pathfindResultDispatcher.dispose()
    }
}