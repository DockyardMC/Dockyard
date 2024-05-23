package io.github.dockyardmc.extentions

fun Double.truncate(decimals: Int): String {
    return String.format("%.${decimals}f", this)
}