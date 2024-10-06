package io.github.dockyardmc.pathfinding.rules

import io.github.dockyardmc.pathfinding.Node
import io.github.dockyardmc.pathfinding.Pathfinder
import io.github.dockyardmc.pathfinding.PathfindingRule

class CuttingWallsRule: PathfindingRule {
    override fun canPathfind(node: Node, current: Node, pathfinder: Pathfinder): Boolean {
        val south = node.location.add(0, 1, 1)
        val east = node.location.add(1, 1, 0)
        val north = node.location.add(0, 1, -1)
        val west = node.location.add(-1, 1, 0)

        if(!node.location.toVector3().isDiagonalTo(current.location.toVector3())) return true
        var cornerCount = 0

        if(!south.block.isAir()) cornerCount++
        if(!east.block.isAir()) cornerCount++
        if(!north.block.isAir()) cornerCount++
        if(!west.block.isAir()) cornerCount++

        return cornerCount < 2
    }
}