package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.pathing.filter.PathFilter
import de.metaphoriker.pathetic.api.pathing.filter.PathValidationContext
import io.github.dockyardmc.pathfinding.PathfindingHelper.isTraversable

class RequiredHeightPathfindingFilter(val height: Int = 2) : PathFilter {

    override fun filter(context: PathValidationContext): Boolean {

        val location = PatheticPlatformDockyard.toLocation(context.position)
        val block = location.block
        for (i in 1 until height + 1) {
            if (block.registryBlock.tags.contains("minecraft:fences")) return false
            val blockAbove = location.add(0, i, 0).block
            val traversable = !isTraversable(blockAbove, location)
            if (!traversable) return false
        }

        return true
    }

}