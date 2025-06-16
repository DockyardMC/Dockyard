package io.github.dockyardmc.entity.ai.test.nodes

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ai.EntityBehaviourNode
import io.github.dockyardmc.entity.ai.EntityBehaviourResult
import io.github.dockyardmc.entity.ai.test.WardenBehaviourCoordinator
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard.toPathPosition
import io.github.dockyardmc.pathfinding.PathfindingHelper
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.utils.debug

class WardenWalkAroundBehaviour(val coordinator: WardenBehaviourCoordinator) : EntityBehaviourNode() {

    override val interruptible: Boolean = true
    var failedTimes: Int = 0
    var scorerMultiplier = 1f

    override fun getScorer(entity: Entity): Float {
        return 0.05f * if(scorerMultiplier < 1f) 0f else 1f
    }

    override fun onStart(entity: Entity) {
        var foundPath = false

        coordinator.navigator.navigationCompleteDispatcher.subscribe {
            getBehaviourFuture().complete(EntityBehaviourResult.SUCCESS)
        }
        coordinator.navigator.navigationNodeStepDispatcher.subscribe {
            entity.playSoundToViewers(Sound(Sounds.ENTITY_WARDEN_STEP))
        }

        entity.location.getBlocksInRadius(10).shuffled().forEach blockLoop@{ location ->
            if(foundPath) {
                return@blockLoop
            }

            if (!PathfindingHelper.isTraversable(location.block, location)) return@blockLoop
            val start = entity.location.getBlockLocation().subtract(0, 1, 0).toPathPosition()
            val end = location.toPathPosition()

            coordinator.navigator.pathfinder.findPath(start, end, coordinator.navigator.filters).thenAccept { result ->
                if (!result.successful()) {
                    return@thenAccept
                }
                if(foundPath) return@thenAccept

                foundPath = true
                debug("Path found: ${result.path.length()}", true)
                coordinator.navigator.cancelNavigating()
                coordinator.navigator.updatePathfindingPath(location)
            }
        }
    }

    override fun onBackstageTick(tick: Int) {
        if (failedTimes >= 5 && scorerMultiplier == 1f) {
            scorerMultiplier = 0f
            debug("<red>5 Failed attempts, scorerMultiplier set to 0", true)
            return
        }

        if (scorerMultiplier < 1f) {
            scorerMultiplier += 0.005f
            debug("<dark_gray>scorerMultiplier now $scorerMultiplier", true)
            if (scorerMultiplier == 1f) {
                debug("<lime>scorerMultiplier back to 1", true)
                failedTimes = 0
            }
        }
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