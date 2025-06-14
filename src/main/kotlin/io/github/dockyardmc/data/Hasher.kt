package io.github.dockyardmc.data

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.ByteOrder
import java.util.zip.CRC32C

// Loosely based on the Hasher implementation from Minestom (with permission of mattw) which is
// loosely based on Hasher implementation of Guava, licensed under the Apache 2.0 license.
data class Hasher(val crc32c: CRC32C, val buffer: ByteBuf) {
    constructor(): this(CRC32C(), Unpooled.buffer(8).order(ByteOrder.LITTLE_ENDIAN))

    fun update(bytes: Int): Hasher {
        crc32c.update(buffer.array(), 0, bytes)
        buffer.writerIndex(0)
        buffer.readerIndex(0)
        return this
    }

    fun putByte(byte: Byte): Hasher {
        crc32c.update(byte.toInt())
        return this
    }

    fun putShort(short: Short): Hasher {
        buffer.writeShort(short.toInt())
        return update(Short.SIZE_BYTES)
    }

    fun putInt(int: Int): Hasher {
        buffer.writeInt(int)
        return update(Int.SIZE_BYTES)
    }

    fun putIntBytes(i: Int): Hasher {
        putByte(i.toByte())
        putByte((i shr 8).toByte())
        putByte((i shr 16).toByte())
        putByte((i shr 24).toByte())
        return this
    }

    fun putLong(long: Long): Hasher {
        buffer.writeLong(long)
        return update(Long.SIZE_BYTES)
    }

    fun putFloat(float: Float): Hasher {
        return putInt(float.toRawBits())
    }

    fun putDouble(double: Double): Hasher {
        return putLong(double.toRawBits())
    }

    fun putChar(c: Char): Hasher {
        putByte(c.code.toByte())
        putByte((c.code ushr 8).toByte())
        return this
    }

    fun putChars(string: String): Hasher {
        for (element in string) this.putChar(element)
        return this
    }

    fun putBytes(bytes: ByteArray?): Hasher {
        crc32c.update(bytes)
        return this
    }

    fun hash(): Int {
        return crc32c.value.toInt()
    }
}