package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.extentions.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException

fun <T> ByteBuf.readLengthPrefixed(maxLength: Int, reader: (ByteBuf) -> T): T {
    val length = this.readVarInt()

    if(length > maxLength) throw DecoderException("Value is too long (length: ${length}, max: ${maxLength})")

    val availableBytes = asByteBuf().readableBytes()
    if(length > availableBytes) throw DecoderException("Value is too long (length: ${length}, available: ${maxLength})")

    val value = reader.invoke(this)
    if(this.readableBytes() != availableBytes - length) throw DecoderException("Value is too short (length: $length, available: $availableBytes)")

    return value
}