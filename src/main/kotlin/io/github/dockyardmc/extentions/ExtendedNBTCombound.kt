package io.github.dockyardmc.extentions

import io.github.dockyardmc.nbt.NbtBuilder
import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.BinaryTagType
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.ListBinaryTag

fun <T : BinaryTag> CompoundBinaryTag.putList(name: String, type: BinaryTagType<T>, list: List<T>): CompoundBinaryTag {
    return this.put(name, ListBinaryTag.listBinaryTag(type, list))
}


fun CompoundBinaryTag.modify(compound: NbtBuilder.() -> Unit): CompoundBinaryTag {
    val builder = NbtBuilder()
    compound.invoke(builder)

    return this.put(builder.build())
}