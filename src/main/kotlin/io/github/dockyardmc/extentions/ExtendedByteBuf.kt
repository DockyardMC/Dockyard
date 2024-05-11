package io.github.dockyardmc.extentions

import io.github.dockyardmc.player.ProfilePropertyMap
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.DecoderException
import log
import org.jglrxavpok.hephaistos.nbt.CompressedProcesser
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTWriter
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
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

fun ByteBuf.writeProfileProperties(propertyMap: ProfilePropertyMap) {

    this.writeUtf(propertyMap.name)
    this.writeVarInt(propertyMap.properties.size)

    // Properties
    propertyMap.properties.forEach {
        this.writeUtf(it.name)
        this.writeUtf(it.value)
        this.writeBoolean(it.isSigned)
        if(it.isSigned && it.signature != null) {
            this.writeUtf(it.signature)
        }
    }
}


fun ByteBuf.writeByteArray(bs: ByteArray) {
    this.writeVarInt(bs.size)
    this.writeBytes(bs)
}

fun ByteBuf.readByteArray(): ByteArray {
    val len = this.readVarInt()
    return readBytes(len).toByteArraySafe()
}

fun ByteBuf.writeNBT(nbt: NBT) {

    val outputStream = ByteArrayOutputStream()
    try {
        val writer = NBTWriter(outputStream, CompressedProcesser.NONE)
        writer.writeNamed("", nbt)
        writer.close()
    } finally {
        var outData = outputStream.toByteArray() //this is 0a00156d696e656. I need this to be 0a6d696e656 (so without the 0015 after 0a)

        // Since 1.20.2 (Protocol 764) NBT sent over the network has been updated to exclude the name from the root TAG_COMPOUND
        // ┌───────────┬────────┬────────────────┬──────────────┬───────────┐
        // │  Version  │ TypeID │ Length of Name │     Name     │  Payload  │
        // ├───────────┼────────┼────────────────┼──────────────┼───────────┤
        // │ < 1.20.2  │ 0x0a   │ 0x00 0x00      │ Empty String │ 0x02 0x09 │
        // │ >= 1.20.2 │ 0x0a   │ N/A            │ N/A          │ 0x02 0x09 │
        // └───────────┴────────┴────────────────┴──────────────┴───────────┘

        val list = outData.toMutableList()
        list.removeAt(1)
        list.removeAt(1)
        outData = list.toByteArray()
        writeBytes(outData)
    }
}


// Thanks to Kev (kev_dev) for pointing this out cause I think I would have gone mad otherwise

fun ByteBuf.readVarLong(): Long {
    var value: Long = 0
    var position = 0
    var currentByte: Byte
    while (true) {
        currentByte = readByte()
        value = value or ((currentByte.toInt() and SEGMENT_BITS.toInt()).toLong() shl position)
        if (currentByte.toInt() and CONTINUE_BIT == 0) break
        position += 7
        if (position >= 64) throw RuntimeException("VarLong is too big")
    }
    return value
}

fun ByteBuf.writeVarLong(value: Long) {
    var writtenValue = value
    while (true) {
        if (writtenValue and SEGMENT_BITS.toLong().inv() == 0L) {
            writeLong(writtenValue)
            return
        }
        writeLong(writtenValue and SEGMENT_BITS.toLong() or CONTINUE_BIT.toLong())

        // Note: ushr means that the sign bit is shifted with the rest of the number rather than being left alone
        writtenValue = writtenValue ushr 7
    }
}

inline fun <reified T : Enum<T>> ByteBuf.readEnum(): T = T::class.java.enumConstants[readVarInt()]

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

fun ByteBuf.writeStringArray(list: MutableList<String>) {
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