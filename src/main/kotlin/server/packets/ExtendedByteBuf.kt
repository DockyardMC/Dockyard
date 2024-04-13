package io.github.dockyard.server.packets

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import java.nio.charset.StandardCharsets
import kotlin.experimental.inv


private const val SEGMENT_BITS: Byte = 0x7F
private const val CONTINUE_BIT = 0x80
fun ByteBuf.readVarInt(): Int {
    var value = 0
    var position = 0
    var currentByte: Byte
    while (true) {
        currentByte = readByte()
        value = value or (currentByte.toInt() and SEGMENT_BITS.toInt() shl position)
        if (currentByte.toInt() and CONTINUE_BIT == 0) break
        position += 7
        if (position >= 32) throw java.lang.RuntimeException("VarInt is too big")
    }
    return value
}
fun ByteBuf.writeVarInt(int: Int) {
    var value = int
    while (true) {
        if (value and SEGMENT_BITS.inv().toInt() == 0) {
            writeByte(value)
            return
        }
        writeByte(value and SEGMENT_BITS.toInt() or CONTINUE_BIT)
        value = value ushr 7
    }
}

fun ByteBuf.readUtf() = readUtf(Short.MAX_VALUE.toInt())
fun ByteBuf.readUtf(i: Int): String {
    val maxSize = i * 3
    val size = this.readVarInt()
    if (size > maxSize) throw DecoderException("The received string was longer than the allowed $maxSize ($size > $maxSize)")
    if (size < 0) throw DecoderException("The received string's length was smaller than 0")
    val string = this.toString(this.readerIndex(), size, StandardCharsets.UTF_8)
    this.readerIndex(this.readerIndex() + size)
    if (string.length > i) throw DecoderException("The received string was longer than the allowed (${string.length} > $i)")
    return string
}