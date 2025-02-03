package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.factory.PathfinderInitializer
import de.metaphoriker.pathetic.api.pathing.Pathfinder
import de.metaphoriker.pathetic.api.pathing.configuration.PathfinderConfiguration

class PathfinderInitializer: PathfinderInitializer {

    override fun initialize(pathfinder: Pathfinder, configuration: PathfinderConfiguration) {
        pathfinder.registerPathfindingHook(PathfindingHook())
    }

}