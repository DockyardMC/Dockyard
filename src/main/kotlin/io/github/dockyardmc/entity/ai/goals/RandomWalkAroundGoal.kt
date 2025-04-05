package io.github.dockyardmc.entity.ai.goals

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ai.AIGoal
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.randomInt
import io.github.dockyardmc.pathfinding.Navigator
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard.toPathPosition
import io.github.dockyardmc.pathfinding.PathfindingHelper

class RandomWalkAroundGoal(override var entity: Entity, override var priority: Int, val navigator: Navigator) : AIGoal() {

    val chancePerTick = 35
    val startingLocation = entity.location
    var hasFinishedWalking = false

    override fun startCondition(): Boolean {
        return randomInt(chancePerTick, 100) == chancePerTick
    }

    override fun start() {
        hasFinishedWalking = false
        var locationToPathfindTo: Location? = null
        startingLocation.getBlocksInRadius(10).shuffled().forEach { location ->
            if (!PathfindingHelper.isTraversable(location.block, location)) return@forEach
            val start = entity.location.getBlockLocation().subtract(0, 1, 0).toPathPosition()
            val end = location.toPathPosition()

            navigator.pathfinder.findPath(start, end, navigator.filters).thenAccept { result ->
                if (!result.successful()) return@thenAccept
                locationToPathfindTo = location
            }
        }

        if (locationToPathfindTo == null) {
            hasFinishedWalking = true
            return
        }

        navigator.updatePathfindingPath(locationToPathfindTo!!)
        navigator.navigationCompleteDispatcher.subscribe {
            hasFinishedWalking = true
        }
    }

    override fun end() {
        navigator.cancelNavigating()
        hasFinishedWalking = false
    }

    override fun endCondition(): Boolean {
        return hasFinishedWalking
    }

    override fun tick() {

    }

}

