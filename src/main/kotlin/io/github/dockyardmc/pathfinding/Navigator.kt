package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.pathing.filter.filters.PassablePathFilter
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard.toLocation
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard.toPathPosition
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.scheduler.SchedulerTask
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.utils.locationLerp
import io.github.dockyardmc.utils.vectors.Vector3f

class Navigator(val entity: Entity, val ticksPerBlock: Int) {

    var state = State.IDLE
        private set

    private var path = mutableListOf<Location>()
    private var newPathQueue = mutableListOf<Location>()
    private val pathfinder = Pathfinder.createPathfinder {
        async(true)
        fallback(true)
    }

    private var calculatingNewPath: Boolean = false
    private var currentTask: SchedulerTask? = null

    var currentNavigationNodeIndex = 0

    fun updatePathfindingPath(target: Location) {
        val start = entity.location.getBlockLocation().subtract(0, 1, 0).toPathPosition()
        val end = target.toPathPosition()

        pathfinder.findPath(start, end, listOf(PassablePathFilter(), RequiredHeightPathfindingFilter(), RequiredSizePathfindingFilter(2, 2))).thenAccept { result ->
            if (result.hasFailed()) {
                path.clear()
                debug("<red>Pathfinding failed", true)
                return@thenAccept
            }

            if (result.hasFallenBack()) debug("<yellow>pathfinding has fallen back", true)

            val newPath = result.path.map { it.toLocation() }.toMutableList()
            newPathQueue = newPath
            if (state == State.IDLE) startNavigating()
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

        currentTask = entity.world.scheduler.runRepeating(ticksPerBlock.ticks) { task ->

            if (state == State.IDLE) {
                cancelNavigating()
                return@runRepeating
            }

            path.forEach { loc ->
                loc.world.spawnParticle(normalizePathLocation(loc), Particles.FLAME, Vector3f(), 0f, 1)
            }

            val currentStepPosition = entity.location
            val nextStepPosition = path.getOrNull(currentNavigationNodeIndex)

            if (nextStepPosition == null) {
                state = State.IDLE
                return@runRepeating
            }

            var j = 0
            entity.world.scheduler.repeat(ticksPerBlock, 1.ticks) { lerpLoop ->
                j++
                val progress = j / ticksPerBlock.toDouble()
                val interpolated = locationLerp(currentStepPosition, normalizePathLocation(nextStepPosition), progress.toFloat())
                val direction = interpolated.toVector3d() - (entity.location).toVector3d()
                entity.teleport(interpolated.setDirection(direction))

                if (j == ticksPerBlock) {
                    currentNavigationNodeIndex++
                    updatePathWhileNavigating()
                }
            }
        }
    }

    fun cancelNavigating() {
        currentTask?.cancel()
        currentTask = null
        debug("<dark_red><bold>Canceled navigating", true)
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

}

