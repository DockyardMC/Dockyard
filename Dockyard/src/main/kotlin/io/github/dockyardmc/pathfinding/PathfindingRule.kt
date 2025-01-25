package io.github.dockyardmc.pathfinding

interface PathfindingRule {

    fun canPathfind(node: Node, current: Node, pathfinder: Pathfinder): Boolean

}