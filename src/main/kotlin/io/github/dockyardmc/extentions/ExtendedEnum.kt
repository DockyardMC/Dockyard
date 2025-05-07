package io.github.dockyardmc.extentions

inline fun <reified T : Enum<T>> enumRandom(): T = enumValues<T>().random()