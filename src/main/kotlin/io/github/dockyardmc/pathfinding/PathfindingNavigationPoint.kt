package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.provider.NavigationPoint
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.world.block.Block

class PathfindingNavigationPoint(val block: Block, val location: Location): NavigationPoint {

    override fun isTraversable(): Boolean {
        return PathfindingHelper.isTraversable(block, location)
    }
}