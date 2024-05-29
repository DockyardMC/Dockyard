package io.github.dockyardmc.location

import io.github.dockyardmc.extentions.truncate
import io.netty.buffer.ByteBuf
import kotlin.math.roundToInt

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

fun ByteBuf.writeLocation(location: Location, includingPitchYaw: Boolean = true) {
    this.writeDouble(location.x)
    this.writeDouble(location.y)
    this.writeDouble(location.z)
    this.writeByte((location.yaw  * 256 / 360).toInt())
    this.writeByte((location.pitch  * 256 / 360).toInt())
}