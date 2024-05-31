package io.github.dockyardmc.utils

import io.github.dockyardmc.location.Location

object MathUtils {

    fun getRelativeCoords(current: Double, previous: Double): Int {
        return ((current * 32 - previous * 32) * 128).toInt()
    }

    fun getRelativeLocation(current: Location, previous: Location): Location {
        val x = getRelativeCoords(current.x, previous.x)
        val y = getRelativeCoords(current.y, previous.z)
        val z = getRelativeCoords(current.y, previous.z)
        return Location(x, y, z)
    }

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