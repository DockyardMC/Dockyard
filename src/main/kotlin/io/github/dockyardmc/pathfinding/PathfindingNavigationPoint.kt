package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.provider.NavigationPoint
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.location.Location

class PathfindingNavigationPoint(val block: Block, val location: Location): NavigationPoint {

    override fun isTraversable(): Boolean {
        return PathfindingHelper.isTraversable(block, location)
    }
}