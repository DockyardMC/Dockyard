package io.github.dockyardmc.pathfinding

import cz.lukynka.bindables.BindableDispatcher
import de.metaphoriker.pathetic.api.pathing.Pathfinder
import de.metaphoriker.pathetic.api.pathing.filter.PathFilter
import de.metaphoriker.pathetic.api.pathing.result.PathfinderResult
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.events.EntityNavigatorPickOffsetEvent
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard.toLocation
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard.toPathPosition
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.scheduler.SchedulerTask
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.UsedAfterDisposedException
import io.github.dockyardmc.utils.getEntityEventContext
import io.github.dockyardmc.utils.locationLerp

class Navigator(val entity: Entity, var speedTicksPerBlock: Int, val pathfinder: Pathfinder, val filters: List<PathFilter>) : Disposable {

    var state = State.IDLE
        private set

    val pathfindResultDispatcher = BindableDispatcher<PathfinderResult>()
    val navigationCompleteDispatcher = BindableDispatcher<PathfindingStep>()
    val navigationNodeStepDispatcher = BindableDispatcher<PathfindingStep>()

    private var isCurrentlyPathfinding: Boolean = false

    private var path = mutableListOf<Location>()
    private var newPathQueue = mutableListOf<Location>()

    private var currentTask: SchedulerTask? = null
    private var currentInterpolationTask: SchedulerTask? = null
    val navigationSchedulerTask get() = currentTask

    private var currentNavigationNodeIndex = 0

    val currentPath get() = path.toList()

    fun updatePathfindingPath(target: Location) {
        if (state == State.DISPOSED) throw UsedAfterDisposedException(this)
        if (isCurrentlyPathfinding) return
        val start = entity.location.getBlockLocation().subtract(0, 1, 0).toPathPosition()
        val end = target.toPathPosition()

        isCurrentlyPathfinding = true
        pathfinder.findPath(start, end, filters).thenAccept { result ->
            isCurrentlyPathfinding = false
            pathfindResultDispatcher.dispatch(result)

            if (result.hasFailed()) {
                path.clear()
                cancelNavigating()
                return@thenAccept
            }

            val newPath = result.path.map { pathPosition ->
                 val event = EntityNavigatorPickOffsetEvent(entity, this, pathPosition.toLocation(), getEntityEventContext(entity))
                Events.dispatch(event)

                event.location
            }.toMutableList()

            newPathQueue = newPath
            if (currentTask == null) startNavigating()
        }
    }

    private fun updatePathWhileNavigating() {
        if (newPathQueue.isNotEmpty()) {
            path.clear()
            path.addAll(newPathQueue)
            newPathQueue.clear()
            currentNavigationNodeIndex = if (path.size >= 5) 2 else 1
        }
    }

    private fun startNavigating() {
        if (state == State.DISPOSED) throw UsedAfterDisposedException(this)
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
            val dispatcherData = PathfindingStep(currentStepPosition, nextStepPosition)
            navigationNodeStepDispatcher.dispatch(dispatcherData)

            if (nextStepPosition == null) {
                navigationCompleteDispatcher.dispatch(dispatcherData)
                state = State.IDLE
                return@runRepeating
            }

            var j = 0
            currentInterpolationTask = entity.world.scheduler.repeat(speedTicksPerBlock, 1.ticks) { _ ->
                if(entity.isDead) return@repeat
                j++
                val progress = j / speedTicksPerBlock.toDouble()
                val interpolated = locationLerp(currentStepPosition, normalizePathLocation(nextStepPosition), progress.toFloat())
                val direction = interpolated.toVector3d() - (entity.location).toVector3d()
                val location = interpolated.setDirection(direction)
                entity.teleport(location)

                if (j == speedTicksPerBlock) {
                    currentNavigationNodeIndex++
                    updatePathWhileNavigating()
                }
            }
        }
    }

    fun cancelNavigating() {
        currentTask?.cancel()
        currentInterpolationTask?.cancel()
        currentTask = null
        currentInterpolationTask = null
    }

    enum class State {
        IDLE,
        NAVIGATING,
        DISPOSED
    }

    private fun normalizePathLocation(location: Location): Location {
        return location.add(0.5, 1.0, 0.5)
    }

    private fun unNormalizePathLocation(location: Location): Location {
        return location.subtract(0.5, 1.0, 0.5)
    }

    data class PathfindingStep(val currentStep: Location, val nextStep: Location?)

    override fun dispose() {
        cancelNavigating()
        pathfindResultDispatcher.dispose()
        navigationCompleteDispatcher.dispose()
        navigationNodeStepDispatcher.dispose()
        path.clear()
        newPathQueue.clear()
    }
}

