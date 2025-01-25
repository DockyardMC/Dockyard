package io.github.dockyardmc.protocol.writers

import io.netty.buffer.ByteBuf


fun ByteBuf.writeByteArray(bs: ByteArray) {
    this.writeVarInt(bs.size)
    this.writeBytes(bs)
}

fun ByteBuf.readByteArray(): ByteArray {
    val size = this.readVarInt()
    val bytes = this.readBytes(size)
    return bytes.array()
}

fun ByteBuf.writeByteBuf(byteBuf: ByteBuf) {
    this.writeBytes(byteBuf)
}

fun ByteBuf.writeByte(byte: Byte) {
    this.writeByte(byte.toInt())
}

fun ByteBuf.readRemainingBytesAsByteArray(): ByteArray {
    val bytes = ByteArray(this.readableBytes())
    this.readBytes(bytes)
    return bytes
}
