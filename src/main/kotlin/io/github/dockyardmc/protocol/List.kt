package io.github.dockyardmc.protocol


import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
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
fun <T> ByteBuf.writeList(list: List<T>, kFunction2: (ByteBuf, T) -> ByteBuf) {
    this.writeVarInt(list.size)
    list.forEach { value ->
        kFunction2.invoke(this, value)
    }
}

@JvmName("writeList2")
fun <T> ByteBuf.writeList(list: List<T>, kFunction2: KFunction2<ByteBuf, T, Unit>) {
    this.writeVarInt(list.size)
    list.forEach { value ->
        kFunction2.invoke(this, value)
    }
}


@JvmName("writeArray1")
fun <T> ByteBuf.writeArray(list: Array<T>, kFunction2: (ByteBuf, T) -> ByteBuf) {
    this.writeVarInt(list.size)
    list.forEach { value ->
        kFunction2.invoke(this, value)
    }
}

@JvmName("writeArray1")
fun <T> ByteBuf.writeArray(list: Array<T>, kFunction2: KFunction2<ByteBuf, T, Unit>) {
    this.writeVarInt(list.size)
    list.forEach { value ->
        kFunction2.invoke(this, value)
    }
}