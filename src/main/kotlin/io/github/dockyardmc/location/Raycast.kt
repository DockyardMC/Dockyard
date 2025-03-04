package io.github.dockyardmc.location

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.vectors.Vector3d
import io.github.dockyardmc.utils.vectors.Vector3f
import kotlin.math.floor

fun blockRaycast(origin: Location, direction: Vector3d, maxDistance: Double, stepSize: Double = 0.1): Pair<Location, io.github.dockyardmc.world.block.Block>? {
    var currentPosition = origin
    var distanceTraveled = 0.0

    while (distanceTraveled < maxDistance) {
        val stepVector = direction.normalized() * Vector3d(stepSize)
        currentPosition = currentPosition.add(stepVector)
        distanceTraveled += stepSize

        val result = hitSolidBlock(currentPosition)

        if (result.first) {
            return currentPosition to result.second
        }
    }

    return null
}

fun blockRaycast(entity: Entity, maxDistance: Double, stepSize: Double = 0.1): Pair<Location, io.github.dockyardmc.world.block.Block>? {
    val location = entity.location.add(Vector3f(0f, entity.type.dimensions.eyeHeight, 0f))
    val result = blockRaycast(location, entity.getFacingDirectionVector().toVector3d(), maxDistance) ?: return null
    return result.first to result.second
}

fun hitSolidBlock(position: Location): Pair<Boolean, io.github.dockyardmc.world.block.Block> {
    val blockX = floor(position.x).toInt()
    val blockY = floor(position.y).toInt()
    val blockZ = floor(position.z).toInt()

    val block = position.world.getBlock(blockX, blockY, blockZ)
    val isSolid = block.registryBlock.isSolid
    return if (!isSolid) (true to Blocks.AIR.toBlock()) else (false to block)
}