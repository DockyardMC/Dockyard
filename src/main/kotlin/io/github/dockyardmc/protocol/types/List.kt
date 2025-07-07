package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf

inline fun <T> ByteBuf.readList(reader: (ByteBuf) -> T): List<T> {
    val list = mutableListOf<T>()
    val size = this.readVarInt()
    for (i in 0 until size) {
        list.add(reader.invoke(this))
    }
    return list.toList()
}

@JvmName("writeList1")
inline fun <T> ByteBuf.writeList(list: Collection<T>, writer: (ByteBuf, T) -> Unit) {
    this.writeVarInt(list.size)
    list.forEach { value ->
        writer.invoke(this, value)
    }
}

@JvmName("writeList3")
inline fun <T> ByteBuf.writeList(list: Collection<T>, writer: (T, ByteBuf) -> Unit) {
    this.writeVarInt(list.size)
    list.forEach { value ->
        writer.invoke(value, this)
    }
}

inline fun <T> ByteBuf.writeRawList(list: Collection<T>, write: T.(ByteBuf) -> Unit) {
    list.forEach {
        it.write(this)
    }
}