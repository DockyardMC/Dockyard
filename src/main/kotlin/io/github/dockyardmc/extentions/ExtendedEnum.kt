package io.github.dockyardmc.extentions

inline fun <reified T : Enum<T>> enumRandom(): T = enumValues<T>().random()

inline fun <reified T : Enum<T>> T.next(): T {
    val values = enumValues<T>()
    val nextOrdinal = (this.ordinal + 1) % values.size
    return values[nextOrdinal]
}

inline fun <reified T : Enum<T>> T.previous(): T {
    val values = enumValues<T>()
    val size = values.size
    // (this.ordinal - 1 + size) ensures the result is always non-negative before the modulo
    val previousOrdinal = (this.ordinal - 1 + size) % size
    return values[previousOrdinal]
}