package io.github.dockyardmc.protocol.writers

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import java.nio.charset.StandardCharsets

fun ByteBuf.readString() = readStringSize(Short.MAX_VALUE.toInt())
fun ByteBuf.readStringSize(i: Int): String {
    val maxSize = i * 3
    val size = this.readVarInt()
    if (size > maxSize) throw DecoderException("The received string was longer than the allowed $maxSize ($size > $maxSize)")
    if (size < 0) throw DecoderException("The received string's length was smaller than 0")
    val string = this.toString(this.readerIndex(), size, StandardCharsets.UTF_8)
    this.readerIndex(this.readerIndex() + size)
    if (string.length > i) throw DecoderException("The received string was longer than the allowed (${string.length} > $i)")
    return string
}

fun ByteBuf.writeString(text: String) {
    val utf8Bytes = text.toByteArray(StandardCharsets.UTF_8)
    val length = utf8Bytes.size
    this.writeVarInt(length)
    this.writeBytes(utf8Bytes)
}