package io.github.dockyardmc.extentions

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.maths.positiveCeilDiv
import io.github.dockyardmc.registry.Registry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.BinaryTagUtils
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.Unpooled
import io.netty.handler.codec.DecoderException
import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.CompoundBinaryTag
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.reflect.KClass

private const val SEGMENT_BITS: Int = 0x7F
private const val CONTINUE_BIT: Int = 0x80
private const val MAXIMUM_VAR_INT_SIZE = 5

inline fun <T> ByteBuf.readList(reader: (ByteBuf) -> T): List<T> {
    val list = mutableListOf<T>()
    val size = this.readVarInt()
    for (i in 0 until size) {
        list.add(reader.invoke(this))
    }
    return list.toList()
}

fun ByteBuf.writeTextComponent(component: Component) {
    component.italic = false
    this.writeNBT(component.toNBT())
}

fun ByteBuf.writeTextComponent(text: String) {
    this.writeTextComponent(text.toComponent())
}

fun ByteBuf.readTextComponent(): Component {
    return this.readNBTCompound().toComponent()
}

fun ByteBuf.writeColor(color: CustomColor) {
    this.writeInt(color.getPackedInt())
}

fun ByteBuf.readUUID(): UUID {
    val most = this.readLong()
    val least = this.readLong()
    return UUID(most, least)
}

fun ByteBuf.writeUUID(uuid: UUID) {
    this.writeLong(uuid.mostSignificantBits)
    this.writeLong(uuid.leastSignificantBits)
}

fun ByteBuf.writeUUIDArray(uuids: Collection<UUID>) {
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

inline fun byteBufBytes(writer: (ByteBuf) -> Unit): ByteArray {
    val tempBuffer = Unpooled.buffer()
    writer.invoke(tempBuffer)
    return tempBuffer.array()
}

fun ByteBuf.writeLongArray(array: List<Long>) {
    this.writeVarInt(array.size)
    array.forEach { this.writeLong(it) }
}

fun ByteBuf.readByteArray(): ByteArray {
    val len = this.readVarInt()
    val buffer = readBytes(len)
    val byteArray = buffer.toByteArraySafe()
    buffer.release()
    return byteArray
}

fun ByteBuf.readNBT(): BinaryTag {
    try {
        val typeId = this.readByte().toInt()
        val type = BinaryTagUtils.nbtTypeFromId(typeId)

        val inputStream = ByteBufInputStream(this) // bro is greedy and takes ALL THE BYTES to himself >:(
        val nbt = type.read(DataInputStream(inputStream))

        // read the rest of the bytes leftover, put them back and reset the reader index
        this.readerIndex(writerIndex() - inputStream.available())

        return nbt
    } catch (ex: Exception) {
        log("Failed to read NBT ($ex). Buffer state: readableBytes=${this.readableBytes()}", LogType.FATAL)
        throw ex
    }
}

fun ByteBuf.readNBTCompound(): CompoundBinaryTag {
    return this.readNBT() as CompoundBinaryTag
}

fun ByteBuf.writeNBT(nbt: BinaryTag) {
    this.writeNBT(nbt as CompoundBinaryTag)
}

fun ByteBuf.writeNBT(nbt: CompoundBinaryTag) {

    val outputStream = ByteArrayOutputStream()
    try {
        BinaryTagIO.writer().writeNameless(nbt, outputStream, BinaryTagIO.Compression.NONE)
    } finally {
        this.writeBytes(outputStream.toByteArray())
    }
}

fun ByteBuf.readFixedBitSet(i: Int): BitSet {
    val bs = ByteArray(positiveCeilDiv(i, 8))
    this.readBytes(bs)
    return BitSet.valueOf(bs)
}

fun ByteBuf.readInstant(): kotlinx.datetime.Instant = kotlinx.datetime.Instant.fromEpochMilliseconds(this.readLong())

fun ByteBuf.writeVarLong(long: Long): ByteBuf {
    var modLong = long
    while (true) {
        if (modLong and -0x80L == 0L) {
            this.writeByte(modLong.toInt())
            break
        }
        this.writeByte((modLong and 0x7FL).toInt() or 0x80)
        modLong = modLong ushr 7
    }
    return this
}

fun ByteBuf.readVarLong(): Long {
    var result = 0L
    for (shift in 0 until 56 step 7) {
        val b = this.readByte()
        result = result or ((b.toLong() and 0x7F) shl shift)
        if (b >= 0) return result
    }
    return result or ((this.readByte().toLong() and 0xFF) shl 56)
}

fun hasContinuationBit(byte: Byte): Boolean = byte.toInt() and 0x80 == 128

inline fun <reified T : Enum<T>> ByteBuf.readEnum(): T = T::class.java.enumConstants[readVarInt()]
fun <T : Enum<T>> ByteBuf.readEnumClass(kClass: KClass<out T>): T = kClass.java.enumConstants[readVarInt()]
inline fun <reified T : Enum<T>> ByteBuf.readByteEnum(): T = T::class.java.enumConstants[readByte().toInt()]

inline fun <reified T : Enum<T>> ByteBuf.writeEnum(value: T) {
    this.writeVarInt(value.ordinal)
}

fun <T : Enum<T>> ByteBuf.writeByteEnum(value: T) {
    this.writeByte(value.ordinal)
}

fun ByteBuf.readVarInt(): Int {
    val readable = this.readableBytes()
    if (readable == 0) throw DecoderException("Invalid VarInt")

    // decode only one byte first as this is the most common size of varints
    var current = this.readByte().toInt()
    if ((current and CONTINUE_BIT) != 128) {
        return current
    }

    // no point in while loop that has higher overhead instead of for loop with max size of the varint
    val maxRead = MAXIMUM_VAR_INT_SIZE.coerceAtMost(readable)
    var varInt = current and SEGMENT_BITS
    for (i in 1..<maxRead) {
        current = this.readByte().toInt()
        varInt = varInt or ((current and SEGMENT_BITS) shl i * 7)
        if (current and CONTINUE_BIT != 128) {
            return varInt
        }
    }
    throw DecoderException("Invalid VarInt")
}

fun ByteBuf.writeStringArray(list: Collection<String>) {
    writeVarInt(list.size)
    list.forEach { writeString(it) }
}


// dark magic but its 2.05 nanoseconds per write
// https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
// little bit modified to write bytes directly because kotlin fucks up the byte order
fun ByteBuf.writeVarInt(int: Int) {
    when {
        // 1-byte
        int and (-1 shl 7) == 0 -> {
            this.writeByte(int)
        }

        // 2-byte
        int and (-1 shl 14) == 0 -> {
            val w = (int and SEGMENT_BITS or CONTINUE_BIT) shl 8 or (int ushr 7)
            this.writeShort(w)
        }

        // 3-byte
        int and (-1 shl 21) == 0 -> {
            this.writeByte(int and SEGMENT_BITS or CONTINUE_BIT)
            this.writeByte((int ushr 7) and SEGMENT_BITS or CONTINUE_BIT)
            this.writeByte(int ushr 14)
        }

        // 4-byte
        int and (-1 shl 28) == 0 -> {
            this.writeByte(int and SEGMENT_BITS or CONTINUE_BIT)
            this.writeByte((int ushr 7) and SEGMENT_BITS or CONTINUE_BIT)
            this.writeByte((int ushr 14) and SEGMENT_BITS or CONTINUE_BIT)
            this.writeByte(int ushr 21)
        }

        // 5-byte
        else -> {
            this.writeByte(int and SEGMENT_BITS or CONTINUE_BIT)
            this.writeByte((int ushr 7) and SEGMENT_BITS or CONTINUE_BIT)
            this.writeByte((int ushr 14) and SEGMENT_BITS or CONTINUE_BIT)
            this.writeByte((int ushr 21) and SEGMENT_BITS or CONTINUE_BIT)
            this.writeByte(int ushr 28)
        }
    }
}

fun ByteBuf.readString() = readString(Short.MAX_VALUE.toInt())

fun ByteBuf.readUtfAndLength() = readUtfAndLength(Short.MAX_VALUE.toInt())

fun ByteBuf.readString(i: Int): String {
    val maxSize = i * 3
    val size = this.readVarInt()
    if (size > maxSize) throw DecoderException("The received string was longer than the allowed $maxSize ($size > $maxSize)")
    if (size < 0) throw DecoderException("The received string's length was smaller than 0")
    val string = this.toString(this.readerIndex(), size, StandardCharsets.UTF_8)
    this.readerIndex(this.readerIndex() + size)
    if (string.length > i) throw DecoderException("The received string was longer than the allowed (${string.length} > $i)")
    return string
}

@Suppress("UNCHECKED_CAST")
fun <T : RegistryEntry> ByteBuf.readRegistryEntry(registry: Registry<*>): T {
    return registry.getByProtocolId(this.readVarInt()) as T
}

fun ByteBuf.writeRegistryEntry(entry: RegistryEntry) {
    this.writeVarInt(entry.getProtocolId())
}

fun ByteBuf.readRemainingBytesAsByteArray(): ByteArray {
    val bytes = ByteArray(this.readableBytes())
    this.readBytes(bytes)
    return bytes
}

fun ByteBuf.readUtfAndLength(i: Int): Pair<String, Int> {
    val maxSize = i * 3
    val size = this.readVarInt()
    if (size > maxSize) throw DecoderException("The received string was longer than the allowed $maxSize ($size > $maxSize)")
    if (size < 0) throw DecoderException("The received string's length was smaller than 0")
    val string = this.toString(this.readerIndex(), size, StandardCharsets.UTF_8)
    this.readerIndex(this.readerIndex() + size)
    if (string.length > i) throw DecoderException("The received string was longer than the allowed (${string.length} > $i)")
    return string to size
}

fun ByteBuf.writeString(text: String): ByteBuf {
    val utf8Bytes = text.toByteArray(StandardCharsets.UTF_8)
    val length = utf8Bytes.size
    this.writeVarInt(length)
    this.writeBytes(utf8Bytes)
    return this
}

fun ByteBuf.toByteArraySafe(): ByteArray {
    if (this.hasArray()) {
        return this.array()
    }

    val bytes = ByteArray(this.readableBytes())
    this.getBytes(this.readerIndex(), bytes)

    return bytes
}

fun ByteArray.toByteBuf(): ByteBuf = Unpooled.copiedBuffer(this)

fun ByteBuf.writeByte(byte: Byte) {
    this.writeByte(byte.toInt())
}

fun Byte.toBoolean(): Boolean {
    return this == 1.toByte()
}

fun Boolean.toByte(): Byte {
    return if (this) 1.toByte() else 0.toByte()
}