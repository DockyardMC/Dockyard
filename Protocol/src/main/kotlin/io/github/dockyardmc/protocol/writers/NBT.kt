package io.github.dockyardmc.protocol.writers

import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


fun ByteBuf.writeNamedBinaryTag(nbt: NBT) {
    this.writeNBT(nbt)
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


fun ByteBuf.readNBT(): NBT {
    val buffer = this
    val nbtReader = NBTReader(object : InputStream() {

        override fun read(): Int = buffer.readByte().toInt() and 0xFF
        override fun available(): Int = buffer.readableBytes()

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
