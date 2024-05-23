package io.github.dockyardmc.location

import io.github.dockyardmc.extentions.truncate

class Location(
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float = 0f,
    var pitch: Float = 0f
) {
    constructor(
        x: Int,
        y: Int,
        z: Int,
        yaw: Float = 0f,
        pitch: Float = 0f
    ): this(x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch)

    override fun toString(): String {
        return "Location(${x.truncate(2)}, ${y.truncate(2)}, ${z.truncate(2)}, yaw: $yaw, pitch: $pitch)"
    }
}