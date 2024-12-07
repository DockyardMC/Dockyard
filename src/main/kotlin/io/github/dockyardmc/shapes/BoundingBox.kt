package io.github.dockyardmc.shapes

import io.github.dockyardmc.utils.vectors.Vector3d

data class BoundingBox(
    val minX: Double,
    val maxX: Double,
    val minY: Double,
    val maxY: Double,
    val minZ: Double,
    val maxZ: Double,
) {
    constructor(
        minX: Int,
        maxX: Int,
        minY: Int,
        maxY: Int,
        minZ: Int,
        maxZ: Int,
    ) : this(
        minX.toDouble(),
        maxX.toDouble(),
        minY.toDouble(),
        maxY.toDouble(),
        minZ.toDouble(),
        maxZ.toDouble()
    )

    constructor(min: Vector3d, max: Vector3d): this(
        min.x,
        max.x,
        min.y,
        max.y,
        min.z,
        max.z
    )
}

data class Rectangle(
    val minX: Double,
    val minY: Double,
    val maxX: Double,
    val maxY: Double,
) {
    constructor(
        minX: Int,
        maxX: Int,
        minY: Int,
        maxY: Int,
    ) : this(
        minX.toDouble(),
        maxX.toDouble(),
        minY.toDouble(),
        maxY.toDouble(),
    )
}