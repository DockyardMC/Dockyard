package io.github.dockyardmc.utils

import io.netty.buffer.ByteBuf

data class Velocity(var x: Int, var y: Int, var z: Int) {
}

fun ByteBuf.writeVelocity(velocity: Velocity) {
    this.writeShort(velocity.x)
    this.writeShort(velocity.y)
    this.writeShort(velocity.z)
}