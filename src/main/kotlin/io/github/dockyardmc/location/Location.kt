package io.github.dockyardmc.location

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
}