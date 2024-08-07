package io.github.dockyardmc.utils

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.world.World
import io.netty.buffer.ByteBuf
import kotlin.math.sqrt

data class Vector3(
    var x: Int,
    var y: Int,
    var z: Int,
) {
    operator fun minus(vector: Vector3): Vector3 {
        val subVector = this.copy()
        subVector.x -= vector.x
        subVector.y -= vector.y
        subVector.z -= vector.z
        return subVector
    }

    constructor() : this(0, 0, 0)
}

data class Vector3f(
    var x: Float,
    var y: Float,
    var z: Float,
) {
    constructor(single: Float) : this(single, single, single)

    fun normalize(): Vector3f {
        val vector = this
        val magnitude = sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z).toFloat()
        return if (magnitude != 0.0f) {
            Vector3f(vector.x / magnitude, vector.y / magnitude, vector.z / magnitude)
        } else {
            vector
        }
    }
}

fun ByteBuf.writeShortVector3(vector3: Vector3) {
    this.writeShort(vector3.x)
    this.writeShort(vector3.y)
    this.writeShort(vector3.z)
}

fun ByteBuf.writeVector3f(vector3: Vector3f) {
    this.writeFloat(vector3.x)
    this.writeFloat(vector3.y)
    this.writeFloat(vector3.z)
}

fun Vector3.toLocation(world: World): Location = Location(this.x, this.y, this.z, world)

fun Vector3f.toLocation(world: World): Location = Location(this.x.toDouble(), this.y.toDouble(), this.z.toDouble(), world)

fun Location.toVector3(): Vector3 = Vector3(this.x.toInt(), this.y.toInt(), this.z.toInt())

fun Location.toVector3f(): Vector3f = Vector3f(this.x.toFloat(), this.y.toFloat(), this.z.toFloat())


fun ByteBuf.writeVector3(vector3: Vector3) {
    this.writeVarInt(vector3.x)
    this.writeVarInt(vector3.y)
    this.writeVarInt(vector3.z)
}

fun ByteBuf.readVector3(): Vector3 = Vector3(this.readVarInt(), this.readVarInt(), this.readVarInt())

fun ByteBuf.readBlockPosition(): Vector3 {
    val value: Long = this.readLong()
    val x = (value shr 38).toInt()
    val y = (value shl 52 shr 52).toInt()
    val z = (value shl 26 shr 38).toInt()
    return Vector3(x, y, z)
}


data class Vector2(
    var x: Float,
    var y: Float,
)