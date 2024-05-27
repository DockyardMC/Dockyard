package io.github.dockyardmc.utils

import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.*
import java.io.IOException
import java.io.OutputStream


fun ByteBuf.writeMSNBT(value: NBT) {
    val buffer = this
    val nbtWriter = NBTWriter(object : OutputStream() {
        override fun write(b: Int) {
            buffer.writeByte(b)
        }
    }, CompressedProcesser.NONE)
    try {
        if (value === NBTEnd) {
            // Kotlin - https://discord.com/channels/706185253441634317/706186227493109860/1163703658341478462
            buffer.writeByte(NBTType.TAG_End.ordinal)
        } else {
            buffer.writeByte(value.ID.ordinal)
            nbtWriter.writeRaw(value)
        }
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}
