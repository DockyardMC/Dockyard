package io.github.dockyardmc.utils

import java.lang.Math

object Math {

    fun percent(max: Double, part: Double): Double {
        return (part / max) * 100
    }

    fun positiveCeilDiv(i: Int, j: Int): Int {
        return -Math.floorDiv(-i, j)
    }
}