package io.github.dockyardmc.extentions

import org.jglrxavpok.hephaistos.collections.ImmutableLongArray
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound


fun MutableNBTCompound.put(key: String, long: Long) {
    this.put(key, NBT.Long(long))
}

fun MutableNBTCompound.put(key: String, longArray: LongArray) {
    this.put(key, NBT.LongArray(ImmutableLongArray(*longArray)))
}
