package io.github.dockyardmc.extentions

fun Boolean.toInt(): Int {
    return when(this) {
        true -> 1
        false -> 0
    }
}

fun Int.toBoolean(): Boolean {
    return when(this) {
        1 -> true
        0 -> false
        else -> true
    }
}