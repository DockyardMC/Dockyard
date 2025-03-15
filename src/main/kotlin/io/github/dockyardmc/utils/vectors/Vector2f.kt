package io.github.dockyardmc.utils.vectors

import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable

@Serializable
data class Vector2f(
    var x: Float,
    var y: Float,
) : NetworkWritable {
    operator fun minus(vector: Vector2f): Vector2f {
        val subVector = this.copy()
        subVector.x -= vector.x
        subVector.y -= vector.y
        return subVector
    }

    operator fun plus(vector: Vector2f): Vector2f {
        val subVector = this.copy()
        subVector.x += vector.x
        subVector.y += vector.y
        return subVector
    }

    operator fun minusAssign(vector: Vector2f) {
        x -= vector.x
        y -= vector.y
    }

    operator fun plusAssign(vector: Vector2f) {
        x -= vector.x
        y -= vector.y
    }

    operator fun times(vector: Vector2f): Vector2f {
        val subVector = this.copy()
        subVector.x *= vector.x
        subVector.y *= vector.y
        return subVector
    }

    operator fun timesAssign(vector: Vector2f) {
        x *= vector.x
        y *= vector.y
    }

    operator fun div(vector: Vector2f): Vector2f {
        val subVector = this.copy()
        subVector.x /= vector.x
        subVector.y /= vector.y
        return subVector
    }

    operator fun divAssign(vector: Vector2f) {
        x /= vector.x
        y /= vector.y
    }

    val isZero: Boolean get() = x == 0f && y == 0f

    fun toVector2(): Vector2 {
        return Vector2(this.x.toInt(), this.y.toInt())
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(this.x)
        buffer.writeFloat(this.y)
    }

    companion object : NetworkReadable<Vector2f> {

        override fun read(buffer: ByteBuf): Vector2f {
            return Vector2f(
                buffer.readFloat(),
                buffer.readFloat(),
            )
        }
    }
}