package io.github.dockyardmc.location

import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.Vector3f
import io.netty.buffer.ByteBuf
import kotlin.math.pow
import kotlin.math.sqrt

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

    // Method to add a Vector3f to this Location
    fun add(vector: Vector3f): Location {
        return Location(this.x + vector.x, this.y + vector.y, this.z + vector.z, this.yaw, this.pitch)
    }

    fun add(vector: Vector3): Location {
        return Location(this.x + vector.x, this.y + vector.y, this.z + vector.z, this.yaw, this.pitch)
    }

    // Method to clone the Location
    fun clone(): Location {
        return Location(this.x, this.y, this.z, this.yaw, this.pitch)
    }

    fun distance(other: Location): Double {
        //surly its not just me that pronounces it "squirt"
        return sqrt((this.x - other.x).pow(2.0) + (this.y - other.y).pow(2.0) + (this.z - other.z).pow(2.0))
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