package io.github.dockyardmc.pathfinding

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.utils.ChunkUtils.floor
import io.github.dockyardmc.utils.Vector3d
import io.github.dockyardmc.utils.toVector3
import io.github.dockyardmc.utils.toVector3d
import io.github.dockyardmc.world.World
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class GroundNodeGenerator(location: Location) : NodeGenerator {

    override val hasGravitySnap: Boolean = true
    val maxFallDistance: Int = 5
    val epsilon = 0.000001

    var tempNode: Node = Node(Location(0, 0, 0, location.world), 0.0, 0.0, NodeType.WALK, null)

    override fun getWalkable(world: World, visited: Set<Node>, current: Node, goal: Location, boundingBox: Entity.BoundingBox): Collection<Node> {
        val nearby = mutableListOf<Node>()
        tempNode = Node(Location(0, 0, 0, goal.world), 0.0, 0.0, NodeType.WALK, current)

        var stepSize = max(floor(boundingBox.maxX / 2), 1)
        if(stepSize < 1) stepSize = 1

        for (x in -stepSize..stepSize) {
            for (z in -stepSize..stepSize) {
                if (x == 0 && z == 0) continue
                val cost = sqrt((x * x + z * z).toDouble()) * 0.98

                val floorPointX: Double = current.blockX + 0.5 + x
                var floorPointY: Double = current.blockY.toDouble()
                val floorPointZ: Double = current.blockZ + 0.5 + z

                val optionalFloorPointY: Double = gravitySnap(world, floorPointX, floorPointY, floorPointZ, boundingBox, maxFallDistance.toDouble()) ?: continue
                floorPointY = optionalFloorPointY

                val floorPoint = Vector3d(floorPointX, floorPointY, floorPointZ)

                val nodeWalk: Node? = createWalk(world, floorPoint.toLocation(world), boundingBox, cost, current, goal, visited)
                if (nodeWalk != null && !visited.contains(nodeWalk)) nearby.add(nodeWalk)
                for (i in 1..1) {
                    var jumpPoint: Location = Vector3d(current.blockX + 0.5 + x, current.blockY + i.toDouble(), current.blockZ + 0.5 + z).toLocation(world)
                    val jumpPointY: Double = gravitySnap(world, jumpPoint.x, jumpPoint.y, jumpPoint.z, boundingBox, maxFallDistance.toDouble()) ?: continue
                    jumpPoint = jumpPoint.clone().apply { y = jumpPointY }

                    if (floorPoint.toLocation(world).getBlock() != jumpPoint.getBlock()) {
                        val nodeJump: Node? = createJump(world, jumpPoint, boundingBox, cost + 0.2, current, goal, visited)
                        if (nodeJump != null && !visited.contains(nodeJump)) nearby.add(nodeJump)
                    }
                }
            }
        }
        return nearby
    }

    override fun gravitySnap(
        world: World,
        pointX: Double,
        pointY: Double,
        pointZ: Double,
        boundingBox: Entity.BoundingBox,
        maxFall: Double,
    ): Double? {
        TODO("Not yet implemented")
    }



    fun createWalk(world: World, location: Location, boundingBox: Entity.BoundingBox, cost: Double, start: Node, goal: Location, closed: Set<Node>): Node? {

        val node = newNode(start, cost, location, goal)
        if(closed.contains(node)) return null

        if (abs(location.y - start.y) > epsilon && location.y < start.y) {
            if (start.y - location.y > maxFallDistance) return null
            if (!canMoveTowards(world, Vector3d(start.x, start.y, start.z).toLocation(world), location.clone().apply { y = start.y }, boundingBox)) return null
            node.type = NodeType.FALL
        } else {
            if (!canMoveTowards(world, Vector3d(start.x, start.y, start.z).toLocation(world), location, boundingBox)) return null
        }

        return node
    }

    fun createJump(world: World, location: Location, boundingBox: Entity.BoundingBox, cost: Double, start: Node, goal: Location, closed: Set<Node>): Node? {

        if(abs(location.y - start.y) < epsilon) return null
        if (location.y - start.y > 2) return null
        if (location.blockX != start.blockX && start.blockZ != start.blockZ) return null

        val node = newNode(start, cost, location, goal)
        if(closed.contains(node)) return null

        if (pointInvalid(world, location.toVector3d(), boundingBox)) return null
        if (pointInvalid(world, Vector3d(start.x, start.y + 1.0, start.z), boundingBox)) return null

        node.type = NodeType.JUMP
        return node
    }

    private fun newNode(current: Node, cost: Double, point: Location, goal: Location): Node {
        tempNode.g = current.g + cost
        tempNode.h = heuristic(point, goal)
        tempNode.location = point

        val newNode: Node = tempNode
        tempNode = Node(Location(0, 0, 0, goal.world), 0.0, 0.0, NodeType.WALK, current)

        return newNode
    }
}