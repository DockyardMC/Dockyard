package io.github.dockyardmc.pathfinding.rules

import io.github.dockyardmc.location.isEmpty
import io.github.dockyardmc.pathfinding.Node
import io.github.dockyardmc.pathfinding.Pathfinder
import io.github.dockyardmc.pathfinding.PathfindingRule

class InsideBlockRule: PathfindingRule {

    override fun canPathfind(node: Node, current: Node, pathfinder: Pathfinder): Boolean {
        var canPathfind = true
        val block = node.location.add(0, 1, 0).getBlock()
        if(!block.isEmpty()) canPathfind = false

        return canPathfind
    }
}