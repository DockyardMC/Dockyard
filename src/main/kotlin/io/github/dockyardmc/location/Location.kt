package io.github.dockyardmc.location

import io.github.dockyardmc.extentions.truncate
import io.netty.buffer.ByteBuf

//TODO Add world
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

fun ByteBuf.writeLocation(location: Location, rotDelta: Boolean = false) {
    this.writeLocationWithoutRot(location)
    if(rotDelta) {
        this.writeByte((location.yaw  * 256f / 360f).toInt())
        this.writeByte((location.pitch  * 256f / 360f).toInt())
    } else {
        this.writeByte(location.yaw.toInt())
        this.writeByte(location.pitch.toInt())
    }
}

fun ByteBuf.writeLocationWithoutRot(location: Location) {
    this.writeDouble(location.x)
    this.writeDouble(location.y)
    this.writeDouble(location.z)
}