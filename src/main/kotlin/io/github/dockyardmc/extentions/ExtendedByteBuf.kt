package io.github.dockyardmc.extentions

import io.github.dockyardmc.utils.MathUtils
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import org.jglrxavpok.hephaistos.nbt.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import kotlin.experimental.inv

private const val SEGMENT_BITS: Byte = 0x7F
private const val CONTINUE_BIT = 0x80

fun ByteBuf.readUUID(): UUID {
    val most = this.readLong()
    val least = this.readLong()
    return UUID(most, least)
}

fun ByteBuf.writeUUID(uuid: UUID) {
    this.writeLong(uuid.mostSignificantBits)
    this.writeLong(uuid.leastSignificantBits)
}

fun ByteBuf.writeUUIDArray(uuids: List<UUID>) {
    this.writeVarInt(uuids.size)
    uuids.forEach { this.writeUUID(it) }
}

fun ByteBuf.writeByteArray(bs: ByteArray) {
    this.writeVarInt(bs.size)
    this.writeBytes(bs)
}

fun ByteBuf.writeVarIntArray(array: List<Int>) {
    this.writeVarInt(array.size)
    array.forEach { this.writeVarInt(it) }
}

fun ByteBuf.writeLongArray(array: LongArray) {
    this.writeLongArray(array.toList())
}

fun ByteBuf.writeLongArray(array: List<Long>) {
    this.writeVarInt(array.size)
    array.forEach { this.writeLong(it) }
}

fun ByteBuf.readByteArray(): ByteArray {
    val len = this.readVarInt()
    return readBytes(len).toByteArraySafe()
}

fun ByteBuf.readNBT(): NBT {
    val buffer = this
    val nbtReader = NBTReader(object : InputStream() {
        override fun read(): Int {
            return buffer.readByte().toInt() and 0xFF
        }

        override fun available(): Int {
            return buffer.readableBytes()
        }
    }, CompressedProcesser.NONE)
    return try {
        val tagId: Byte = buffer.readByte()
        if (tagId.toInt() == NBTType.TAG_End.ordinal) NBTEnd else nbtReader.readRaw(tagId.toInt())
    } catch (e: IOException) {
        throw java.lang.RuntimeException(e)
    } catch (e: NBTException) {
        throw java.lang.RuntimeException(e)
    }
}


fun ByteBuf.writeNBT(nbt: NBT, truncateRootTag: Boolean = true) {

    val outputStream = ByteArrayOutputStream()
    try {
        val writer = NBTWriter(outputStream, CompressedProcesser.NONE)
        writer.writeNamed("", nbt)
        writer.close()
    } finally {
        if (truncateRootTag) {
            var outData = outputStream.toByteArray()

            // Since 1.20.2 (Protocol 764) NBT sent over the network has been updated to exclude the name from the root TAG_COMPOUND
            // ┌───────────┬────────┬────────────────┬──────────────┬───────────┐
            // │  Version  │ TypeID │ Length of Name │     Name     │  Payload  │
            // ├───────────┼────────┼────────────────┼──────────────┼───────────┤
            // │ < 1.20.2  │ 0x0a   │ 0x00 0x00      │ Empty String │ 0x02 0x09 │
            // │ >= 1.20.2 │ 0x0a   │ N/A            │ N/A          │ 0x02 0x09 │
            // └───────────┴────────┴────────────────┴──────────────┴───────────┘

            // Thanks to Kev (kev_dev) for pointing this out because I think I would have gone mad otherwise
            val list = outData.toMutableList()
            list.removeAt(1)
            list.removeAt(1)
            outData = list.toByteArray()
            writeBytes(outData)
        } else {
            writeBytes(outputStream.toByteArray())
        }
    }
}

fun ByteBuf.readFixedBitSet(i: Int): BitSet {
    val bs = ByteArray(MathUtils.positiveCeilDiv(i, 8))
    this.readBytes(bs)
    return BitSet.valueOf(bs)
}

fun ByteBuf.readInstant(): Instant {
    return Instant.ofEpochMilli(this.readLong())
}

fun ByteBuf.writeVarLong(long: Long): ByteBuf {
    var modLong = long
    while (true) {
        if (modLong and -0x80L == 0L) {
            this.writeByte(modLong.toInt())
        }
        this.writeByte((modLong and 0x7FL).toInt() or 0x80)
        modLong = modLong ushr 7
    }
}

fun ByteBuf.readVarLong(): Long {
    var b: Byte
    var long = 0L
    var iteration = 0
    do {
        b = this.readByte()
        long = long or ((b.toInt() and 0x7F).toLong() shl iteration++ * 7)
        if (iteration <= 10) continue
        throw RuntimeException("VarLong too big")
    } while (hasContinuationBit(b))
    return long
}

fun hasContinuationBit(byte: Byte): Boolean {
    return byte.toInt() and 0x80 == 128
}
inline fun <reified T : Enum<T>> ByteBuf.readVarIntEnum(): T = T::class.java.enumConstants[readVarInt()]
inline fun <reified T : Enum<T>> ByteBuf.readByteEnum(): T = T::class.java.enumConstants[readByte().toInt()]

fun <T : Enum<T>> ByteBuf.writeVarIntEnum(value: T) {
    this.writeVarInt(value.ordinal)
}

fun <T : Enum<T>> ByteBuf.writeByteEnum(value: T) {
    this.writeByte(value.ordinal)
}


fun ByteBuf.readVarInt(): Int {
    var value = 0
    var position = 0
    var currentByte: Byte
    while (this.isReadable) {
        currentByte = readByte()
        value = value or (currentByte.toInt() and SEGMENT_BITS.toInt() shl position)
        if (currentByte.toInt() and CONTINUE_BIT == 0) break
        position += 7
        if (position >= 32) throw java.lang.RuntimeException("VarInt is too big")
    }
    return value
}

fun ByteBuf.writeStringArray(list: Collection<String>) {
    writeVarInt(list.size)
    list.forEach { writeUtf(it) }
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
fun ByteBuf.readUtfAndLength() = readUtfAndLength(Short.MAX_VALUE.toInt())
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

fun ByteBuf.readUtfAndLength(i: Int): Pair<String, Int> {
    val maxSize = i * 3
    val size = this.readVarInt()
    if (size > maxSize) throw DecoderException("The received string was longer than the allowed $maxSize ($size > $maxSize)")
    if (size < 0) throw DecoderException("The received string's length was smaller than 0")
    val string = this.toString(this.readerIndex(), size, StandardCharsets.UTF_8)
    this.readerIndex(this.readerIndex() + size)
    if (string.length > i) throw DecoderException("The received string was longer than the allowed (${string.length} > $i)")
    return Pair(string, size)
}

fun ByteBuf.writeUtf(text: String) {
    val utf8Bytes = text.toByteArray(StandardCharsets.UTF_8)
    val length = utf8Bytes.size
    this.writeVarInt(length)
    this.writeBytes(utf8Bytes)
}

fun ByteBuf.toByteArraySafe(): ByteArray {
    if (this.hasArray()) {
        return this.array()
    }

    val bytes = ByteArray(this.readableBytes())
    this.getBytes(this.readerIndex(), bytes)

    return bytes
}