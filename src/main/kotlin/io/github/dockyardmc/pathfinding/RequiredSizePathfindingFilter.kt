package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.pathing.filter.PathFilter
import de.metaphoriker.pathetic.api.pathing.filter.PathValidationContext
import io.github.dockyardmc.pathfinding.PathfindingHelper.isTraversable

class RequiredSizePathfindingFilter(val height: Int = 2, val width: Int = 1) : PathFilter {

    override fun filter(context: PathValidationContext): Boolean {

        val location = PatheticPlatformDockyard.toLocation(context.position)

        for (i in 1 until height + 1) {
            val blockAbove = location.add(0, i, 0)
            val traversable = !isTraversable(blockAbove.block, blockAbove)
            if (!traversable) return false

            for (j in 0 until width) {
                val posX = blockAbove.add(j, 0, 0)
                val negX = blockAbove.add(-j, 0, 0)
                val posZ = blockAbove.add(0, 0, j)
                val negZ = blockAbove.add(0, 0, -j)

                if(isTraversable(posX.block, posX)) return false
                if(isTraversable(negX.block, negX)) return false

                if(isTraversable(posZ.block, posZ)) return false
                if(isTraversable(negZ.block, negZ)) return false
            }
        }
        return true
    }
}