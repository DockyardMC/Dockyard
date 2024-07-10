package io.github.dockyardmc.utils

import io.github.dockyardmc.location.Location
import kotlin.random.Random

object MathUtils {

    fun getRelativeCoords(current: Double, previous: Double): Int = ((current * 32 - previous * 32) * 128).toInt()

    fun remap(value: Int, fromMin: Int, fromMax: Int, toMin: Int, toMax: Int): Int =
        (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin

    fun remap(value: Double, fromMin: Double, fromMax: Double, toMin: Double, toMax: Double): Double =
        (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin

    fun square(num: Double): Double = num * num

    fun toCorrectSlotIndex(slot: Int): Int {
        return when (slot) {
            in 36..44 -> slot - 36
            in 27..35 -> slot - 18
            in 18..26 -> slot
            in 9..17 -> slot + 18
            5 -> 36
            6 -> 37
            7 -> 38
            8 -> 39
            45 -> 40
            else -> 0
        }
    }

    fun toOriginalSlotIndex(correctedSlot: Int): Int {
        return when (correctedSlot) {
            in 0..8 -> correctedSlot + 36
            in 9..17 -> correctedSlot + 18
            in 18..26 -> correctedSlot
            in 27..35 -> correctedSlot - 18
            36 -> 5
            37 -> 6
            38 -> 7
            39 -> 8
            45 -> 40
            else -> 0
        }
    }

    fun getRelativeLocation(current: Location, previous: Location): Location {
        require(current.world == previous.world) { "The two locations need to be in the same world!" }
        val x = getRelativeCoords(current.x, previous.x)
        val y = getRelativeCoords(current.y, previous.z)
        val z = getRelativeCoords(current.y, previous.z)
        return Location(x, y, z, current.world)
    }

    fun percent(max: Double, part: Double): Double = (part / max) * 100

    fun positiveCeilDiv(i: Int, j: Int): Int = -Math.floorDiv(-i, j)

    fun bitsToRepresent(n: Int): Int {
        if(n < 1) throw Exception("n must be greater than 0")
        return Integer.SIZE - Integer.numberOfLeadingZeros(n)
    }

    fun randomInt(min: Int, max: Int): Int = (min..max).shuffled().last()
    fun randomFloat(min: Float, max: Float): Float {
        val random = java.util.Random()
        return min + random.nextFloat() * (max - min)
    }

}