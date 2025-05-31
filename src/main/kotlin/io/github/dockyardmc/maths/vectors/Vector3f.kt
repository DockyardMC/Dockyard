package io.github.dockyardmc.maths.vectors

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.world.World
import io.netty.buffer.ByteBuf
import kotlin.math.pow
import kotlin.math.sqrt

data class Vector3f(
    var x: Float,
    var y: Float,
    var z: Float,
): NetworkWritable {
    constructor() : this(0f, 0f, 0f)
    constructor(x: Int, y: Int, z: Int) : this(x.toFloat(), y.toFloat(), z.toFloat())
    constructor(x: Int, y: Float, z: Int) : this(x.toFloat(), y, z.toFloat())
    constructor(x: Float, y: Int, z: Float) : this(x, y.toFloat(), z)
    constructor(single: Float) : this(single, single, single)


    operator fun minus(vector: Vector3f): Vector3f {
        val subVector = this.copy()
        subVector.x -= vector.x
        subVector.y -= vector.y
        subVector.z -= vector.z
        return subVector
    }

    operator fun plus(vector: Vector3f): Vector3f {
        val subVector = this.copy()
        subVector.x += vector.x
        subVector.y += vector.y
        subVector.z += vector.z
        return subVector
    }

    operator fun minusAssign(vector: Vector3f) {
        x -= vector.x
        y -= vector.y
        z -= vector.z
    }

    operator fun plusAssign(vector: Vector3f) {
        x -= vector.x
        y -= vector.y
        z -= vector.z
    }

    operator fun times(vector: Vector3f): Vector3f {
        val subVector = this.copy()
        subVector.x *= vector.x
        subVector.y *= vector.y
        subVector.z *= vector.z
        return subVector
    }

    operator fun timesAssign(vector: Vector3f) {
        x *= vector.x
        y *= vector.y
        z *= vector.z
    }

    operator fun div(vector: Vector3f): Vector3f {
        val subVector = this.copy()
        subVector.x /= vector.x
        subVector.y /= vector.y
        subVector.z /= vector.z
        return subVector
    }

    operator fun divAssign(vector: Vector3f) {
        x /= vector.x
        y /= vector.y
        z /= vector.z
    }

    fun dot(other: Vector3f): Float = this.x * other.x + this.y * other.y + this.z * other.z

    fun cross(other: Vector3f): Vector3f {
        return Vector3f(
            this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x
        )
    }

    fun distance(other: Vector3f): Double =
        sqrt((this.x - other.x).pow(2f) + (this.y - other.y).pow(2f) + (this.z - other.z).pow(2f)).toDouble()

    fun normalized(): Vector3f {
        val vector = this.copy()
        val magnitude = sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z)
        return if (magnitude != 0.0f) {
            Vector3f(vector.x / magnitude, vector.y / magnitude, vector.z / magnitude)
        } else {
            vector
        }
    }

    val isZero: Boolean get() = x == 0f && y == 0f && z == 0f

    fun toLocation(world: World): Location = Location(this.x, this.y, this.z, world)
    fun toVector3d() = Vector3d(x.toDouble(), y.toDouble(), z.toDouble())
    fun toVector3() = Vector3(x.toInt(), y.toInt(), z.toInt())

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(this.x)
        buffer.writeFloat(this.y)
        buffer.writeFloat(this.z)
    }

    companion object: NetworkReadable<Vector3f> {

        override fun read(buffer: ByteBuf): Vector3f {
            return Vector3f(
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readFloat()
            )
        }
    }
}