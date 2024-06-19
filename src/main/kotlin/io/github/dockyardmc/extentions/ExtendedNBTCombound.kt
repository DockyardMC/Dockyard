package io.github.dockyardmc.extentions

import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound


fun MutableNBTCompound.put(key: String, long: Long) {
    this.put(key, NBT.Long(long))
}
