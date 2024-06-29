package io.github.dockyardmc.location

import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.Vector2
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.Vector3f
import io.netty.buffer.ByteBuf
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

//TODO Add world
class Location(
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float = 0f,
    var pitch: Float = 0f,
) {
    constructor(
        x: Int,
        y: Int,
        z: Int,
        yaw: Float = 0f,
        pitch: Float = 0f,
    ): this(x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch)

    override fun toString(): String =
        "Location(${x.truncate(2)}, ${y.truncate(2)}, ${z.truncate(2)}, yaw: $yaw, pitch: $pitch)"

    fun add(vector: Vector3f): Location =
        Location(this.x + vector.x, this.y + vector.y, this.z + vector.z, this.yaw, this.pitch)

    fun add(vector: Vector3): Location =
        Location(this.x + vector.x, this.y + vector.y, this.z + vector.z, this.yaw, this.pitch)

    fun clone(): Location = Location(this.x, this.y, this.z, this.yaw, this.pitch)

    fun distance(other: Location): Double =
        //surly it's not just me that pronounces it "squirt"
        sqrt((this.x - other.x).pow(2.0) + (this.y - other.y).pow(2.0) + (this.z - other.z).pow(2.0))

    fun centerBlockLocation(): Location = this.apply { x += 0.5; y += 0.5; z += 0.5 }

    fun getRotation(): Vector2 = Vector2(yaw, pitch)


    //TODO Rewrite this, temp stolen from bukkit

    fun setDirection(vector: Vector3f): Location {
        val pi2 = 6.283185307179586
        val x: Double = vector.x.toDouble()
        val z: Double = vector.z.toDouble()
        if (x == 0.0 && z == 0.0) {
            this.yaw = if (vector.y.toDouble() > 0.0) -90.0f else 90.0f
            return this
        }
        val theta = atan2(-x, z)
        this.pitch = Math.toDegrees((theta + pi2) % pi2).toFloat()
        val x2: Double = MathUtils.square(x)
        val z2: Double = MathUtils.square(z)
        val xz = sqrt(x2 + z2)
        this.yaw = Math.toDegrees(atan(-vector.y.toDouble() / xz)).toFloat()
        return this
    }

    fun subtract(vec: Location): Location {
        this.x -= vec.x
        this.y -= vec.y
        this.z -= vec.z
        return this
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