package io.github.dockyardmc.maths

import com.google.common.primitives.Ints.min
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.world.chunk.ChunkUtils.floor
import io.github.dockyardmc.maths.vectors.Vector3f
import java.io.File
import java.security.MessageDigest
import java.util.*
import kotlin.math.*

fun multiplyQuaternions(q1: Quaternion, q2: Quaternion): Quaternion {
    val x = q1.w * q2.x + q1.x * q2.w + q1.y * q2.z - q1.z * q2.y
    val y = q1.w * q2.y - q1.x * q2.z + q1.y * q2.w + q1.z * q2.x
    val z = q1.w * q2.z + q1.x * q2.y - q1.y * q2.x + q1.z * q2.w
    val w = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z
    return Quaternion(x, y, z, w)
}

fun minMax(value: Int, min: Int, max: Int): Int = min(value, max(value, max), min)

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

fun getRelativeLocation(current: Location, previous: Location): Location {
    require(current.world == previous.world) { "The two locations need to be in the same world!" }
    val x = getRelativeCoords(current.x, previous.x)
    val y = getRelativeCoords(current.y, previous.z)
    val z = getRelativeCoords(current.y, previous.z)
    return Location(x, y, z, current.world)
}

fun percent(max: Double, part: Double): Double = part / max * 100.0
fun percent(max: Int, part: Int): Float = part.toFloat() / max.toFloat() * 100
fun percent(max: Float, part: Float): Float = part / max * 100f
fun percent(max: Long, part: Long): Float = part.toFloat() / max.toFloat() * 100L

fun percentOf(percent: Float, max: Double): Double = percent * max

fun positiveCeilDiv(i: Int, j: Int): Int = -Math.floorDiv(-i, j)

fun bitsToRepresent(n: Int): Int {
    if (n < 1) throw Exception("n must be greater than 0")
    return Integer.SIZE - Integer.numberOfLeadingZeros(n)
}

fun isBetween(number: Int, min: Int, max: Int): Boolean {
    return number in min..max
}

fun randomInt(min: Int, max: Int): Int = Random().nextInt(min, max)

fun randomFloat(min: Float, max: Float): Float {
    val random = Random()
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

fun chunkInSpiral(id: Int, xOffset: Int = 0, zOffset: Int = 0): Pair<Int, Int> {
    // if the id is 0 then we know we're in the centre
    if (id == 0) return 0 + xOffset to 0 + zOffset

    val index = id - 1

    // compute radius (inverse arithmetic sum of 8 + 16 + 24 + ...)
    val radius = floor((sqrt(index + 1.0) - 1) / 2) + 1

    // compute total point on radius -1 (arithmetic sum of 8 + 16 + 24 + ...)
    val p = 8 * radius * (radius - 1) / 2

    // points by face
    val en = radius * 2

    // compute de position and shift it so the first is (-r, -r) but (-r + 1, -r)
    // so the square can connect
    val a = (1 + index - p) % (radius * 8)

    return when (a / (radius * 2)) {
        // find the face (0 = top, 1 = right, 2 = bottom, 3 = left)
        0 -> a - radius + xOffset to -radius + zOffset
        1 -> radius + xOffset to a % en - radius + zOffset
        2 -> radius - a % en + xOffset to radius + zOffset
        3 -> -radius + xOffset to radius - a % en + zOffset
        else -> 0 to 0
    }
}