package io.github.dockyardmc.pathfinding

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.Vector3d
import io.github.dockyardmc.world.World

interface NodeGenerator {

    fun getWalkable(
        world: World,
        visited: Set<Node>,
        current: Node,
        goal: Location,
        boundingBox: Entity.BoundingBox,
    ): Collection<Node>

    val hasGravitySnap: Boolean

    fun gravitySnap(
        world: World,
        pointX: Double,
        pointY: Double,
        pointZ: Double,
        boundingBox: Entity.BoundingBox,
        maxFall: Double,
    ): Double?

    //TODO Collision calc
    fun canMoveTowards(world: World, start: Location, end: Location, boundingBox: Entity.BoundingBox): Boolean = world.getBlock(end) === Blocks.AIR

    fun heuristic(node: Location, target: Location): Double = node.distance(target)

    //TODO
    fun pointInvalid(world: World, location: Vector3d, boundingBox: Entity.BoundingBox): Boolean {
//        val iterator: Unit = boundingBox.getBlocks(location)
//        while (iterator.hasNext()) {
//            val block: Unit = iterator.next()
//            if (world.getBlock(block.blockX(), block.blockY(), block.blockZ(), Block.Getter.Condition.TYPE)
//                    .isSolid()
//            ) {
//                return true
//            }
//        }

        return false
    }
}