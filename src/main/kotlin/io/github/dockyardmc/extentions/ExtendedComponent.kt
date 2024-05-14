package io.github.dockyardmc.extentions

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.toJson
import org.jglrxavpok.hephaistos.json.NBTGsonReader
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.StringReader

fun Component.toNBT(): NBT {
    val gsonReader = NBTGsonReader(StringReader(this.toJson().toString()))
    return gsonReader.read<NBTCompound>()
}