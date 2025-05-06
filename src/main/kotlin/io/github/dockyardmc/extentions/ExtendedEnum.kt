package io.github.dockyardmc.extentions

inline fun <reified T : Enum<T>> enumRandom(): T = enumValues<T>().random()

fun toUnsignedLong(x: Int): Long {
    return (x.toLong()) and 0xffffffffL
}