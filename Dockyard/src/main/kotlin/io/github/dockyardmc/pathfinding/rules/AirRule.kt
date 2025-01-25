package io.github.dockyardmc.pathfinding.rules

import io.github.dockyardmc.pathfinding.Node
import io.github.dockyardmc.pathfinding.Pathfinder
import io.github.dockyardmc.pathfinding.PathfindingRule

class AirRule: PathfindingRule {

    override fun canPathfind(node: Node, current: Node, pathfinder: Pathfinder): Boolean {
        return !node.location.block.isAir()
    }
}