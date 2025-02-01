package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.provider.NavigationPoint
import de.metaphoriker.pathetic.api.provider.NavigationPointProvider
import de.metaphoriker.pathetic.api.wrapper.PathPosition

class PathfinderNavigationPointProvider: NavigationPointProvider {

    override fun getNavigationPoint(position: PathPosition): NavigationPoint {
        val location = PatheticPlatformDockyard.toLocation(position)
        return PathfindingNavigationPoint(location.block, location)
    }
}