package io.github.dockyardmc.pathfinding

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.isEmpty
import kotlin.math.sqrt

fun smoothPath(path: List<Location>): List<Location> {
    val smoothedPath = mutableListOf<Location>()
    for (i in 0 until path.size - 2) {
        val current = path[i]
        val next = path[i + 1]
        val nextNext = path[i + 2]
        // Check if a straight line from current to nextNext is clear
        if (isLineClear(current, nextNext)) {
            smoothedPath.add(current)
            smoothedPath.add(nextNext)
//            i += 1 // Skip next point since we jumped to nextNext
            continue
        } else {
            smoothedPath.add(current)
            smoothedPath.add(next)
        }
    }
    smoothedPath.addAll(path.subList(smoothedPath.size, minOf(path.size, smoothedPath.size + 2)))
    return smoothedPath
}

fun isLineClear(from: Location, to: Location): Boolean {
    // Calculate the direction vector from 'from' to 'to'
    val dx = to.x - from.x
    val dy = to.y - from.y
    val dz = to.z - from.z

    // Normalize the direction vector to get a unit vector
    val length = sqrt(dx * dx + dy * dy + dz * dz)
    val stepX = dx / length
    val stepY = dy / length
    val stepZ = dz / length

    // Start at 'from' and move along the line in steps
    var currentX = from.x
    var currentY = from.y
    var currentZ = from.z

    while (true) {
//        // Check if the current position is within the bounds of the world
//        if (currentX < 0 || currentX >= world.width ||
//            currentY < 0 || currentY >= world.height ||
//            currentZ < 0 || currentZ >= world.depth) {
//            return false // Out of bounds
//        }

        // Check if the block at the current position is solid
        if (!from.world.getBlock(currentX.toInt(), currentY.toInt(), currentZ.toInt()).isEmpty()) {
            return false // Obstacle found
        }

        // Move to the next position along the line
        currentX += stepX
        currentY += stepY
        currentZ += stepZ

        // Check if we've reached the 'to' location
        if (currentX == to.x && currentY == to.y && currentZ == to.z) {
            return true // Line is clear
        }
    }
}