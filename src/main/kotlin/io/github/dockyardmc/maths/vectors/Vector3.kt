package io.github.dockyardmc.maths.vectors

import io.github.dockyardmc.extentions.toVector3
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.world.World
import io.netty.buffer.ByteBuf
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class Vector3(
    var x: Int,
    var y: Int,
    var z: Int,
) : NetworkWritable {
    constructor() : this(0, 0, 0)
    constructor(single: Int) : this(single, single, single)

    operator fun minus(vector: Vector3): Vector3 {
        val subVector = this.copy()
        subVector.x -= vector.x
        subVector.y -= vector.y
        subVector.z -= vector.z
        return subVector
    }

    operator fun plus(vector: Vector3): Vector3 {
        val subVector = this.copy()
        subVector.x += vector.x
        subVector.y += vector.y
        subVector.z += vector.z
        return subVector
    }

    operator fun minusAssign(vector: Vector3) {
        x -= vector.x
        y -= vector.y
        z -= vector.z
    }

    operator fun plusAssign(vector: Vector3) {
        x -= vector.x
        y -= vector.y
        z -= vector.z
    }

    operator fun times(vector: Vector3): Vector3 {
        val subVector = this.copy()
        subVector.x *= vector.x
        subVector.y *= vector.y
        subVector.z *= vector.z
        return subVector
    }

    operator fun timesAssign(vector: Vector3) {
        x *= vector.x
        y *= vector.y
        z *= vector.z
    }

    operator fun div(vector: Vector3): Vector3 {
        val subVector = this.copy()
        subVector.x /= vector.x
        subVector.y /= vector.y
        subVector.z /= vector.z
        return subVector
    }

    operator fun divAssign(vector: Vector3) {
        x /= vector.x
        y /= vector.y
        z /= vector.z
    }

    fun dot(other: Vector3): Int = this.x * other.x + this.y * other.y + this.z * other.z

    fun cross(other: Vector3): Vector3 {
        return Vector3(
            this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x
        )
    }

    fun distance(other: Vector3): Double {
        return sqrt(
            (this.x - other.x).toDouble().pow(2) +
                    (this.y - other.y).toDouble().pow(2) +
                    (this.z - other.z).toDouble().pow(2)
        )
    }

    val isZero: Boolean get() = x == 0 && y == 0 && z == 0

    fun distanceSquared(other: Vector3): Int {
        val dx = this.x - other.x
        val dy = this.y - other.y
        val dz = this.z - other.z

        return dx * dx + dy * dy + dz * dz
    }

    fun toLocation(world: World): Location = Location(this.x, this.y, this.z, world)
    fun toVector3d() = Vector3d(x.toDouble(), y.toDouble(), z.toDouble())
    fun toVector3f() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

    fun isDiagonalTo(other: Vector3): Boolean {
        val dx = abs(x - other.x)
        val dy = abs(y - other.y)
        val dz = abs(z - other.z)

        return (dx == 1 && dy == 1 && dz == 0) ||
                (dx == 1 && dz == 1 && dy == 0) ||
                (dy == 1 && dz == 1 && dx == 0)
    }

    fun equalsBlock(end: Location): Boolean {
        return end.x.toInt() == x &&
                end.y.toInt() == y &&
                end.z.toInt() == z
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(this.x)
        buffer.writeVarInt(this.y)
        buffer.writeVarInt(this.z)
    }

    fun writeAsShorts(buffer: ByteBuf) {
        buffer.writeShort(this.x)
        buffer.writeShort(this.y)
        buffer.writeShort(this.z)
    }

    fun toIntArray(): IntArray {
        return intArrayOf(x, y, z)
    }

    companion object : NetworkReadable<Vector3> {

        val ZERO = Vector3(0)

        val CODEC = Codec.INT_ARRAY.transform<Vector3>({ from -> from.toVector3() }, { to -> to.toIntArray() })

        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.VAR_INT, Vector3::x,
            StreamCodec.VAR_INT, Vector3::y,
            StreamCodec.VAR_INT, Vector3::z,
            ::Vector3
        )

        override fun read(buffer: ByteBuf): Vector3 {
            return STREAM_CODEC.read(buffer)
        }
    }
}