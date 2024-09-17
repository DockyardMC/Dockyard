package io.github.dockyardmc.pathfinding

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.rules.AirRule
import io.github.dockyardmc.pathfinding.rules.InsideBlockRule
import io.github.dockyardmc.registry.Blocks
import kotlin.math.abs

data class Node(
    val location: Location,
    var gCost: Double,
    var hCost: Double,
    var parent: Node? = null,
) {
    val cost: Double get() = gCost + hCost

    fun getNearbyLocations(): List<Location> {
        return location.getAdjacentLocations()
    }
}

class Pathfinder(
    val goal: Location,
    val start: Location,
    val rules: MutableList<PathfindingRule> = mutableListOf<PathfindingRule>(AirRule(), InsideBlockRule())
) {
    val open = mutableListOf<Node>(locationToNode(start))
    val closed = mutableListOf<Node>()

    private var path: List<Location>? = null

    fun findPath(): List<Location>? {
        while(path == null) {
            if(open.size == 0) return null
            nextStep()
        }
        return path
    }

    fun nextStep() {
        val current = sortByCost(open, open[0])
        open.remove(current)
        closed.add(current)


        if(current.location.equalsBlock(goal)) {
            current.location.world.setBlock(current.location, Blocks.AMETHYST_BLOCK)
            path = retracePath(locationToNode(start), current)
            return
        }

        val neighbours = current.getNearbyLocations().map { locationToNode(it) }
        neighbours.forEach { node ->

            rules.forEach ruleLoop@{ rule ->
                if(!rule.canPathfind(node, current, this)) return@forEach
            }

            if(closed.containsHashed(node)) return@forEach

            val newMovementCostToNode = current.gCost + costDistance(current.location, node.location)
            if(newMovementCostToNode < node.gCost || !open.containsHashed(node)) {
                node.gCost = newMovementCostToNode
                node.hCost = costDistance(node.location, goal)
                node.parent = current

                if(!open.containsHashed(node)) {
                    open.add(node)
                }
            }
        }
    }

    fun Collection<Node>.containsHashed(node: Node): Boolean {
        val containsHash = "${node.location.blockX}${node.location.blockY}${node.location.blockZ}"
        return this.any { "${it.location.blockX}${it.location.blockY}${it.location.blockZ}" == containsHash }
    }

    fun locationToNode(location: Location): Node {
        return Node(location,
            gCost = costDistance(location, start),
            hCost = costDistance(location, goal),
        )
    }
}

fun retracePath(start: Node, end: Node): List<Location> {
    val path = mutableListOf<Node>()
    var current = end

    while(current != start) {
        path.add(current)
        current = current.parent!!
    }
    return path.map { it.location }.toMutableList().reversed()
}

fun costDistance(nodeA: Location, nodeB: Location): Double {
    val dstX = abs(nodeA.blockX - nodeB.blockX)
    val dstZ = abs(nodeA.blockZ - nodeB.blockZ)

    if(dstX > dstZ) {
        return (14 * dstZ + 10 * (dstX - dstZ)).toDouble()
    }
    return (14 * dstX + 10 * (dstZ - dstX)).toDouble()
}

fun sortByCost(open: List<Node>, current: Node): Node {
    var new: Node = current
    var minCost = current.cost
    var minHCost = current.hCost

    for (node in open) {
        val nodeCost = node.cost
        if (nodeCost < minCost || (nodeCost == minCost && node.hCost < minHCost)) {
            new = node
            minCost = nodeCost
            minHCost = node.hCost
        }
    }

    return new
}