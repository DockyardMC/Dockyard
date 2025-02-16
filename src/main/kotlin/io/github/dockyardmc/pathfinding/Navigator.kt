package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.pathing.Pathfinder
import de.metaphoriker.pathetic.api.pathing.filter.PathFilter
import de.metaphoriker.pathetic.api.pathing.result.PathfinderResult
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard.toLocation
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard.toPathPosition
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.scheduler.SchedulerTask
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.utils.locationLerp

class Navigator(val entity: Entity, val speedTicksPerBlock: Int, val pathfinder: Pathfinder, val filters: List<PathFilter>) {

    var state = State.IDLE
        private set

    private var onPathfindResult: ((PathfinderResult) -> Unit)? = null
    private var onNavigationComplete: (() -> Unit)? = null
    private var onNavigationNodeStep: ((PathfindingStep) -> Unit)? = null

    private var path = mutableListOf<Location>()
    private var newPathQueue = mutableListOf<Location>()

    private var currentTask: SchedulerTask? = null
    val navigationSchedulerTask get() = currentTask

    private var currentNavigationNodeIndex = 0

    val currentPath get() = path.toList()

    fun onPathfindingResult(unit: (PathfinderResult) -> Unit) {
        this.onPathfindResult = unit
    }

    fun onNavigationComplete(unit: () -> Unit) {
        this.onNavigationComplete = unit
    }

    fun onNavigationNodeStep(unit: (PathfindingStep) -> Unit) {
        this.onNavigationNodeStep = unit
    }

    fun updatePathfindingPath(target: Location) {
        val start = entity.location.getBlockLocation().subtract(0, 1, 0).toPathPosition()
        val end = target.toPathPosition()

        pathfinder.findPath(start, end, filters).thenAccept { result ->

            onPathfindResult?.invoke(result)

            if (result.hasFailed()) {
                path.clear()
                cancelNavigating()
                return@thenAccept
            }

            val newPath = result.path.map { it.toLocation() }.toMutableList()
            newPathQueue = newPath
            if (currentTask == null) startNavigating()

        }
    }

    private fun updatePathWhileNavigating() {
        if (newPathQueue.isNotEmpty()) {
            path.clear()
            path.addAll(newPathQueue)
            newPathQueue.clear()
            currentNavigationNodeIndex = if(path.size >= 5) 2 else 1
        }
    }

    private fun startNavigating() {
        state = State.NAVIGATING
        cancelNavigating()
        updatePathWhileNavigating()

        currentTask = entity.world.scheduler.runRepeating(speedTicksPerBlock.ticks) { task ->
            if (state == State.IDLE) {
                cancelNavigating()
                return@runRepeating
            }

            val currentStepPosition = entity.location
            val nextStepPosition = path.getOrNull(currentNavigationNodeIndex)
            if(onNavigationNodeStep != null) {
                val data = PathfindingStep(currentStepPosition, nextStepPosition)
                onNavigationNodeStep!!.invoke(data)
            }

            if (nextStepPosition == null) {
                onNavigationComplete?.invoke()
                state = State.IDLE
                return@runRepeating
            }

            var j = 0
            entity.world.scheduler.repeat(speedTicksPerBlock, 1.ticks) { lerpLoop ->
                j++
                val progress = j / speedTicksPerBlock.toDouble()
                val interpolated = locationLerp(currentStepPosition, normalizePathLocation(nextStepPosition), progress.toFloat())
                val direction = interpolated.toVector3d() - (entity.location).toVector3d()
                entity.teleport(interpolated.setDirection(direction))

                if (j == speedTicksPerBlock) {
                    currentNavigationNodeIndex++
                    updatePathWhileNavigating()
                }
            }
        }
    }

    fun cancelNavigating() {
        currentTask?.cancel()
        currentTask = null
    }

    enum class State {
        IDLE,
        NAVIGATING,
    }

    private fun normalizePathLocation(location: Location): Location {
        return location.add(0.5, 1.0, 0.5)
    }

    private fun unNormalizePathLocation(location: Location): Location {
        return location.subtract(0.5, 1.0, 0.5)
    }

    data class PathfindingStep(val currentStep: Location, val nextStep: Location?)

}

