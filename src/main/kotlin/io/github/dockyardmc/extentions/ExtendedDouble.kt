package io.github.dockyardmc.extentions

import java.util.*

fun Double.round(decimals: Int): Double = Math.round(this * 10 * decimals) / 10.0 * decimals
fun Double.truncate(decimals: Int): String = String.format(Locale.ROOT, "%.${decimals}f", this)
fun Float.round(decimals: Int): Float = Math.round(this * 10 * decimals) / 10.0f * decimals
fun Float.truncate(decimals: Int): String = String.format(Locale.ROOT, "%.${decimals}f", this)