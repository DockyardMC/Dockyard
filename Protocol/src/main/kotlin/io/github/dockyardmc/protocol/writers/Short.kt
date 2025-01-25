package io.github.dockyardmc.protocol.writers

import io.netty.buffer.ByteBuf

fun ByteBuf.writeShort(short: Short) {
    this.writeShort(short.toInt())
}
