package io.github.dockyardmc.pathfinding

import io.github.dockyardmc.location.Location
import kotlin.math.floor

class Node(
    var location: Location,
    var g: Double,
    var h: Double,
    var type: NodeType,
    var parent: Node? = null,
) {

    var hashcode = cantor(
        floor(location.x).toInt(), cantor(
            floor(location.y).toInt(),
            floor(location.z).toInt()
        )
    );

    private fun cantor(a: Int, b: Int): Int {
        val ca = if (a >= 0) 2 * a else -2 * a - 1
        val cb = if (b >= 0) 2 * b else -2 * b - 1
        return (ca + cb + 1) * (ca + cb) / 2 + cb
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other !is Node) return false
        return this.hashCode() == other.hashCode()
    }


    override fun hashCode(): Int = this.hashcode

    val x: Double get() = location.x
    val y: Double get() = location.y
    val z: Double get() = location.z
    val blockX: Int get() = floor(location.x).toInt()
    val blockY: Int get() = floor(location.y).toInt()
    val blockZ: Int get() = floor(location.z).toInt()

}

enum class NodeType {
    WALK,
    JUMP,
    CLIMB,
    CLIMB_WALL,
    SWIM,
    FLY,
    FALL,
    REPATH
}