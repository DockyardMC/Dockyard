package io.github.dockyardmc.extentions

import java.util.Locale
import kotlin.math.pow
import kotlin.math.round

fun Double.round(decimals: Int): Double {
    return 10.0.pow(decimals).let {
        round(this * it) / it
    }
}

fun Double.truncate(decimals: Int): String = String.format(Locale.ROOT, "%.${decimals}f", this)

fun Float.round(decimals: Int): Float {
    return 10f.pow(decimals).let {
        round(this * it) / it
    }
}

fun Float.truncate(decimals: Int): String = String.format(Locale.ROOT, "%.${decimals}f", this)