package io.github.dockyardmc.utils

import net.kyori.adventure.nbt.BinaryTagType
import net.kyori.adventure.nbt.BinaryTagTypes

object BinaryTagUtils {
    val TYPES = listOf<BinaryTagType<*>>(
        BinaryTagTypes.END,
        BinaryTagTypes.BYTE,
        BinaryTagTypes.SHORT,
        BinaryTagTypes.INT,
        BinaryTagTypes.LONG,
        BinaryTagTypes.FLOAT,
        BinaryTagTypes.DOUBLE,
        BinaryTagTypes.BYTE_ARRAY,
        BinaryTagTypes.STRING,
        BinaryTagTypes.LIST,
        BinaryTagTypes.COMPOUND,
        BinaryTagTypes.INT_ARRAY,
        BinaryTagTypes.LONG_ARRAY,
    )

    fun nbtTypeFromId(id: Int): BinaryTagType<*> {
        return TYPES[id]
    }
}