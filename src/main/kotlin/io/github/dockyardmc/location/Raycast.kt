package io.github.dockyardmc.location

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.vectors.Vector3d
import kotlin.math.floor

fun blockRaycast(origin: Location, direction: Location, maxDistance: Double, stepSize: Double = 0.1): Pair<Location, Block>? {
    var currentPosition = origin
    var distanceTraveled = 0.0

    while (distanceTraveled < maxDistance) {
        val stepVector = direction.toVector3d().normalized() * Vector3d(stepSize)
        currentPosition = currentPosition.add(stepVector)
        distanceTraveled += stepSize

        val result = hitSolidBlock(currentPosition)

        if (result.first) {
            return currentPosition to result.second
        }
    }

    return null
}

fun hitSolidBlock(position: Location): Pair<Boolean, Block> {
    val blockX = floor(position.x).toInt()
    val blockY = floor(position.y).toInt()
    val blockZ = floor(position.z).toInt()

    val block = position.world.getBlock(blockX, blockY, blockZ)
    val isSolid = block.registryBlock.isSolid
    return if (!isSolid) (true to Blocks.AIR.toBlock()) else (false to block)
}