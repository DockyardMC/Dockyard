package io.github.dockyardmc.protocol.writers

import io.netty.buffer.ByteBuf

inline fun <reified T : Enum<T>> ByteBuf.readEnum(): T = T::class.java.enumConstants[readVarInt()]
inline fun <reified T : Enum<T>> ByteBuf.readByteEnum(): T = T::class.java.enumConstants[readByte().toInt()]

inline fun <reified T : Enum<T>> ByteBuf.writeEnum(value: T) {
    this.writeVarInt(value.ordinal)
}

fun <T : Enum<T>> ByteBuf.writeByteEnum(value: T) {
    this.writeByte(value.ordinal)
}