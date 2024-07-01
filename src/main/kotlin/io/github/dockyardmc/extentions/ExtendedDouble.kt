package io.github.dockyardmc.extentions

fun Double.truncate(decimals: Int): String = String.format("%.${decimals}f", this)