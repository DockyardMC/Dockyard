package io.github.dockyardmc.protocol.writers

import io.netty.buffer.ByteBuf
import kotlin.reflect.KFunction2

fun <T> ByteBuf.readList(reader: (ByteBuf) -> T): List<T> {
    val list = mutableListOf<T>()
    val size = this.readVarInt()
    for (i in 0 until size) {
        list.add(reader.invoke(this))
    }
    return list.toList()
}

@JvmName("writeList1")
fun <T> ByteBuf.writeList(list: Collection<T>, unit: KFunction2<ByteBuf, T, Unit>) {
    this.writeVarInt(list.size)
    list.forEach { value ->
        unit.invoke(this, value)
    }
}

@JvmName("writeList2")
fun <T> ByteBuf.writeList(list: Collection<T>, unit: KFunction2<T, ByteBuf, Unit>) {
    this.writeVarInt(list.size)
    list.forEach { value ->
        unit.invoke(value, this)
    }
}