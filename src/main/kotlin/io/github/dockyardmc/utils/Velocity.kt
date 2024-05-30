package io.github.dockyardmc.utils

import io.netty.buffer.ByteBuf

fun ByteBuf.writeVelocity(velocity: Vector3) {
    this.writeShort(velocity.x)
    this.writeShort(velocity.y)
    this.writeShort(velocity.z)
}