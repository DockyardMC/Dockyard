package io.github.dockyardmc.pathfinding

import io.github.dockyardmc.location.Location

data class Node(
    val location: Location,
    var gCost: Double,
    var hCost: Double,
    var parent: Node? = null,
) {
    val cost: Double get() = gCost + hCost

    fun getNearbyLocations(): List<Location> {
        return listOf(
            location.getBlockLocation().add(1, 0, 0),
            location.getBlockLocation().add(-1, 0, 0),
            location.getBlockLocation().add(0, 1, 0),
            location.getBlockLocation().add(0, -1, 0),
            location.getBlockLocation().add(0, 0, 1),
            location.getBlockLocation().add(0, 0, -1),

            location.getBlockLocation().add(1, 1, 0),
            location.getBlockLocation().add(-1, 1, 0),
            location.getBlockLocation().add(0, 1, 1),
            location.getBlockLocation().add(0, 1, -1),

            location.getBlockLocation().add(1, -1, 0),
            location.getBlockLocation().add(-1, -1, 0),
            location.getBlockLocation().add(0, -1, 1),
            location.getBlockLocation().add(0, -1, -1),

            location.getBlockLocation().add(1, 0, 1),
            location.getBlockLocation().add(-1, 0, 1),
            location.getBlockLocation().add(1, 0, -1),
            location.getBlockLocation().add(-1, 0, -1),

            location.getBlockLocation().add(1, 1, 1),
            location.getBlockLocation().add(-1, 1, 1),
            location.getBlockLocation().add(1, 1, -1),
            location.getBlockLocation().add(-1, 1, -1),

            location.getBlockLocation().add(1, -1, 1),
            location.getBlockLocation().add(-1, -1, 1),
            location.getBlockLocation().add(1, -1, -1),
            location.getBlockLocation().add(-1, -1, -1)
        )
    }
}