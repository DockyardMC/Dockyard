package io.github.dockyardmc.utils

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.utils.vectors.Vector3f
import java.io.File
import java.lang.IllegalStateException
import java.security.MessageDigest
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun multiplyQuaternions(q1: Quaternion, q2: Quaternion): Quaternion {
    val x = q1.w * q2.x + q1.x * q2.w + q1.y * q2.z - q1.z * q2.y
    val y = q1.w * q2.y - q1.x * q2.z + q1.y * q2.w + q1.z * q2.x
    val z = q1.w * q2.z + q1.x * q2.y - q1.y * q2.x + q1.z * q2.w
    val w = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z
    return Quaternion(x, y, z, w)
}

fun degreesToRadians(degrees: Float): Float = (degrees * (PI / 180.0)).toFloat()

fun eulerToQuaternion(euler: Vector3f): Quaternion =
    eulerToQuaternion(euler.x.toDouble(), euler.y.toDouble(), euler.z.toDouble())

fun eulerToQuaternion(roll: Double, pitch: Double, yaw: Double): Quaternion {
    val cy = cos(yaw * 0.5)
    val sy = sin(yaw * 0.5)
    val cp = cos(pitch * 0.5)
    val sp = sin(pitch * 0.5)
    val cr = cos(roll * 0.5)
    val sr = sin(roll * 0.5)


    val w = cr * cp * cy + sr * sp * sy
    val x = sr * cp * cy - cr * sp * sy
    val y = cr * sp * cy + sr * cp * sy
    val z = cr * cp * sy - sr * sp * cy

    return Quaternion(w.toFloat(), x.toFloat(), y.toFloat(), z.toFloat())
}

fun getRelativeCoords(current: Double, previous: Double): Int = ((current * 32 - previous * 32) * 128).toInt()

fun remap(value: Int, fromMin: Int, fromMax: Int, toMin: Int, toMax: Int): Int =
    (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin

fun remap(value: Double, fromMin: Double, fromMax: Double, toMin: Double, toMax: Double): Double =
    (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin

fun square(num: Double): Double = num * num

fun playerInventoryCorrectSlot(slot: Int): Int {
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
        40 -> 45
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

// percent is float 0f - 1f.
fun percentOf(percent: Float, max: Double): Double = percent * max

fun positiveCeilDiv(i: Int, j: Int): Int = -Math.floorDiv(-i, j)

fun bitsToRepresent(n: Int): Int {
    if (n < 1) throw Exception("n must be greater than 0")
    return Integer.SIZE - Integer.numberOfLeadingZeros(n)
}

fun randomInt(min: Int, max: Int): Int = (min..max).shuffled().last()
fun randomFloat(min: Float, max: Float): Float {
    val random = java.util.Random()
    return min + random.nextFloat() * (max - min)
}

fun getFileHash(file: File, algorithm: String): String {
    val digest = MessageDigest.getInstance(algorithm)
    file.inputStream().use { fis ->
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (fis.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
    }
    return digest.digest().joinToString("") { String.format("%02x", it) }
}

fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
}

fun lerp(a: Double, b: Double, t: Float): Double {
    return a + (b - a) * t
}

fun locationLerp(from: Location, to: Location, t: Float): Location {
    if(from.world != to.world) throw IllegalStateException("Two provided locations are not in the same world")
    val x = lerp(from.x, to.x, t)
    val y = lerp(from.y, to.y, t)
    val z = lerp(from.z, to.z, t)
    return Location(x, y, z, from.world)
}