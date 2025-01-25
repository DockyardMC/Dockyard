package io.github.dockyardmc.scroll.extensions

import org.jglrxavpok.hephaistos.nbt.*
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound

fun MutableNBTCompound.put(key: String, value: Boolean?) {
    if(value == null) return
    this.put(key, NBT.Boolean(value))
}

fun MutableNBTCompound.put(key: String, value: Int?) {
    if(value == null) return
    this.put(key, NBT.Int(value))
}

fun MutableNBTCompound.put(key: String, value: Byte?) {
    if(value == null) return
    this.put(key, NBT.Byte(value))
}

fun MutableNBTCompound.put(key: String, value: Double?) {
    if(value == null) return
    this.put(key, NBT.Double(value))
}

fun MutableNBTCompound.put(key: String, value: Float?) {
    if(value == null) return
    this.put(key, NBT.Float(value))
}

fun MutableNBTCompound.put(key: String, value: String?) {
    if(value == null) return
    this.put(key, NBT.String(value))
}

fun MutableNBTCompound.put(key: String, value: Short?) {
    if(value == null) return
    this.put(key, NBT.Short(value))
}

fun MutableNBTCompound.put(key: String, value: CompoundBuilder?) {
    if(value == null) return
    this.put(key, NBT.Compound(value))
}

fun MutableNBTCompound.put(key: String, value: List<NBTCompound>?) {
    if(value == null) return
    if(value.isEmpty()) return
    this.put(key, NBT.List(NBTType.TAG_Compound, value))
}