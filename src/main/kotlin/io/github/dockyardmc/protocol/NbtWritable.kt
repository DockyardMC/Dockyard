package io.github.dockyardmc.protocol

import net.kyori.adventure.nbt.BinaryTag


interface NbtWritable {

    fun getNbt(): BinaryTag

}