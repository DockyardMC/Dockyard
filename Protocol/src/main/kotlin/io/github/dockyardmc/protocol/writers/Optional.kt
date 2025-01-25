package io.github.dockyardmc.protocol.writers

import io.netty.buffer.ByteBuf
import kotlin.reflect.KFunction2

fun <T> ByteBuf.writeOptional(item: T?, unit: KFunction2<ByteBuf, T, Unit>) {
    val isPresent = item != null
    this.writeBoolean(isPresent)
    if (isPresent) {
        unit.invoke(this, item!!)
    }
}

fun <T> ByteBuf.readOptional(unit: (ByteBuf) -> T): T? {
    val present = this.readBoolean()
    return if(!present) null else unit.invoke(this)
}