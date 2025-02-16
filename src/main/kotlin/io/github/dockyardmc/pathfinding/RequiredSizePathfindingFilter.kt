package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.pathing.filter.PathFilter
import de.metaphoriker.pathetic.api.pathing.filter.PathValidationContext
import io.github.dockyardmc.pathfinding.PathfindingHelper.isTraversable
import java.util.concurrent.atomic.AtomicInteger

class RequiredSizePathfindingFilter(val height: Int = 2, val width: Int = 1) : PathFilter {

    override fun filter(context: PathValidationContext): Boolean {

        val location = PatheticPlatformDockyard.toLocation(context.position)
        val fails = AtomicInteger()
        val failThreshold = 1

        for (i in 1 until height + 1) {
            val blockAbove = location.add(0, i, 0)
            val traversable = !isTraversable(blockAbove.block, blockAbove)
            if (!traversable) return false

            for (j in 0 until width) {
                val posX = blockAbove.add(j, 0, 0)
                val negX = blockAbove.add(-j, 0, 0)
                val posZ = blockAbove.add(0, 0, j)
                val negZ = blockAbove.add(0, 0, -j)

                if(isTraversable(posX.block, posX)) fails.incrementAndGet()
                if(isTraversable(negX.block, negX)) fails.incrementAndGet()

                if(isTraversable(posZ.block, posZ)) fails.incrementAndGet()
                if(isTraversable(negZ.block, negZ)) fails.incrementAndGet()

                if(fails.get() > failThreshold) {
                    return false
                }
            }
        }
        return true
    }
}