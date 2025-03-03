package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.provider.NavigationPoint
import io.github.dockyardmc.world.block.Block
import io.github.dockyardmc.location.Location

class PathfindingNavigationPoint(val block: io.github.dockyardmc.world.block.Block, val location: Location): NavigationPoint {

    override fun isTraversable(): Boolean {
        return PathfindingHelper.isTraversable(block, location)
    }
}