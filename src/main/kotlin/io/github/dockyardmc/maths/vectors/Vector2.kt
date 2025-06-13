package io.github.dockyardmc.maths.vectors

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable

@Serializable
data class Vector2(
    var x: Int,
    var y: Int,
) : NetworkWritable {
    constructor() : this(0, 0)
    constructor(single: Int) : this(single, single)
    operator fun minus(vector: Vector2): Vector2 {
        val subVector = this.copy()
        subVector.x -= vector.x
        subVector.y -= vector.y
        return subVector
    }

    operator fun plus(vector: Vector2): Vector2 {
        val subVector = this.copy()
        subVector.x += vector.x
        subVector.y += vector.y
        return subVector
    }

    operator fun minusAssign(vector: Vector2) {
        x -= vector.x
        y -= vector.y
    }

    operator fun plusAssign(vector: Vector2) {
        x -= vector.x
        y -= vector.y
    }

    operator fun times(vector: Vector2): Vector2 {
        val subVector = this.copy()
        subVector.x *= vector.x
        subVector.y *= vector.y
        return subVector
    }

    operator fun timesAssign(vector: Vector2) {
        x *= vector.x
        y *= vector.y
    }

    operator fun div(vector: Vector2): Vector2 {
        val subVector = this.copy()
        subVector.x /= vector.x
        subVector.y /= vector.y
        return subVector
    }

    operator fun divAssign(vector: Vector2) {
        x /= vector.x
        y /= vector.y
    }

    val isZero: Boolean get() = x == 0 && y == 0

    fun toVector2f(): Vector2f {
        return Vector2f(this.x.toFloat(), this.y.toFloat())
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(this.x)
        buffer.writeVarInt(this.y)
    }

    companion object : NetworkReadable<Vector2> {

        override fun read(buffer: ByteBuf): Vector2 {
            return Vector2(
                buffer.readVarInt(),
                buffer.readVarInt(),
            )
        }
    }
}
