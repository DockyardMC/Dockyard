package io.github.dockyardmc.utils

import io.netty.buffer.ByteBuf

data class Quaternion(
    val x: Float,
    val y: Float,
    val z: Float,
    val w: Float
) {
}

fun ByteBuf.writeQuaternion(quaternion: Quaternion) {
    this.writeFloat(quaternion.x)
    this.writeFloat(quaternion.y)
    this.writeFloat(quaternion.z)
    this.writeFloat(quaternion.w)
}