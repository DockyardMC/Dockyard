package io.github.dockyardmc.extentions

fun Boolean.toInt(): Int {
    return when(this) {
        true -> 1
        false -> 0
    }
}