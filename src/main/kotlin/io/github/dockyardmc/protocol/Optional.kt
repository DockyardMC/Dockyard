package io.github.dockyardmc.protocol

import io.netty.buffer.ByteBuf

@JvmName("writeOptional2")
inline fun <T> ByteBuf.writeOptional(item: T?, kFunction2: (T, ByteBuf) -> Unit) {
    val isPresent = item != null
    this.writeBoolean(isPresent)
    if (isPresent) {
        kFunction2.invoke(item, this)
    }
}

@JvmName("writeOptional3")
inline fun <T> ByteBuf.writeOptional(item: T?, kFunction2: (ByteBuf, T) -> Unit) {
    val isPresent = item != null
    this.writeBoolean(isPresent)
    if (isPresent) {
        kFunction2.invoke(this, item)
    }
}



inline fun <T> ByteBuf.readOptional(unit: (ByteBuf) -> T): T? {
    val present = this.readBoolean()
    return if(!present) null else unit.invoke(this)
}

inline fun <T> ByteBuf.writeOptionalList(list: List<T>?, writer: (ByteBuf, T) -> Unit) {
    val isPresent = list != null
    this.writeBoolean(isPresent)
    if(isPresent) {
        list.forEach { item ->
            writer.invoke(this, item)
        }
    }
}