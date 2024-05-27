package io.github.dockyardmc.utils

object MathUtils {

    fun percent(max: Double, part: Double): Double {
        return (part / max) * 100
    }

    fun positiveCeilDiv(i: Int, j: Int): Int {
        return -Math.floorDiv(-i, j)
    }

    fun bitsToRepresent(n: Int): Int {
        if(n < 1) throw Exception("n must be greater than 0")
        return Integer.SIZE - Integer.numberOfLeadingZeros(n)
    }
}