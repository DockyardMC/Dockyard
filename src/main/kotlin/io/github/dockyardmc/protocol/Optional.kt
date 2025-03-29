package io.github.dockyardmc.protocol

import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3

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