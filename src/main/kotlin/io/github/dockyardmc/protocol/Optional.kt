package io.github.dockyardmc.protocol

import io.netty.buffer.ByteBuf
import kotlin.reflect.KFunction2

fun <T> ByteBuf.writeOptional(item: T?, kFunction2: (ByteBuf, T) -> ByteBuf) {
    val isPresent = item != null
    this.writeBoolean(isPresent)
    if (isPresent) {
        kFunction2.invoke(this, item!!)
    }
}

fun <T> ByteBuf.writeOptional(item: T?, kFunction2: KFunction2<T, ByteBuf, Unit>) {
    val isPresent = item != null
    this.writeBoolean(isPresent)
    if (isPresent) {
        kFunction2.invoke(item!!, this)
    }
}

fun <T> ByteBuf.writeOptional(item: T?, kFunction2: KFunction2<ByteBuf, T, Unit>) {
    val isPresent = item != null
    this.writeBoolean(isPresent)
    if (isPresent) {
        kFunction2.invoke(this, item!!)
    }
}

fun <T> ByteBuf.readOptional(unit: (ByteBuf) -> T): T? {
    val present = this.readBoolean()
    return if(!present) null else unit.invoke(this)
}

fun <T> ByteBuf.writeOptionalList(list: List<T>?, writer: KFunction2<ByteBuf, T, Unit>) {
    val isPresent = list != null
    this.writeBoolean(isPresent)
    if(isPresent) {
        list!!.forEach { item ->
            writer.invoke(this, item)
        }
    }
}