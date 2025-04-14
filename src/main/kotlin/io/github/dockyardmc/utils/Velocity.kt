package io.github.dockyardmc.utils

import io.github.dockyardmc.maths.vectors.Vector3d
import io.netty.buffer.ByteBuf
import kotlin.math.max
import kotlin.math.min

fun ByteBuf.writeVelocity(velocity: Vector3d) {
    this.writeShort(clamp(velocity.x, Short.MIN_VALUE, Short.MAX_VALUE))
    this.writeShort(clamp(velocity.y, Short.MIN_VALUE, Short.MAX_VALUE))
    this.writeShort(clamp(velocity.z, Short.MIN_VALUE, Short.MAX_VALUE))
}

fun clamp(value: Double, min: Short, max: Short): Int {
    return min(max(value, min.toDouble()), max.toDouble()).toInt()
}