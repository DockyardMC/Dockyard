package io.github.dockyardmc.utils

import io.github.dockyardmc.world.Chunk
import kotlin.math.sin
import kotlin.math.sqrt

object ChunkUtils {

    private val MULTIPLY_DE_BRUIJN_BIT_POSITION = intArrayOf(
        0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8,
        31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
    )
    private val SIN = FloatArray(65536) { sin(it.toDouble() * Math.PI * 2.0 / 65536.0).toFloat() }
    const val EPSILON: Float = 1.0E-5F
    private const val TO_RADIANS_FACTOR = Math.PI.toFloat() / 180F


    fun getChunkCoordinate(xz: Int): Int = xz shr 4

    fun sectionRelative(xyz: Int): Int = xyz and 0xF

    fun getChunkIndex(chunk: Chunk): Long = getChunkIndex(chunk.chunkX, chunk.chunkZ)

    fun getChunkCoordinate(xz: Double): Int = getChunkCoordinate(floor(xz).toInt())

    fun getChunkIndex(chunkX: Int, chunkZ: Int): Long = ((chunkX.toLong()) shl 32) or (chunkZ.toLong() and 0xffffffffL)

    fun getChunkX(index: Long): Int = (index shr 32).toInt()

    fun getChunkZ(index: Long): Int = index.toInt()

    fun getChunkCoordsFromIndex(index: Long): Pair<Int, Int> = getChunkX(index) to getChunkZ(index)

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

    @JvmStatic
    fun floor(value: Float): Int {
        val result = value.toInt()
        return if (value < result) result - 1 else result
    }

    @JvmStatic
    fun floor(value: Double): Int {
        val result = value.toInt()
        return if (value < result) result - 1 else result
    }

    @JvmStatic
    fun lfloor(value: Double): Long {
        val result = value.toLong()
        return if (value < result) result - 1 else result
    }

    @JvmStatic
    fun ceil(value: Double): Int {
        val result = value.toInt()
        return if (value > result) result + 1 else result
    }

    @JvmStatic
    fun positiveCeilDivide(x: Int, y: Int): Int = -Math.floorDiv(-x, y)

    @JvmStatic
    @Suppress("MagicNumber")
    fun ceillog2(value: Int): Int {
        val temp = if (isPowerOfTwo(value)) value else roundUpPow2(value)
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(temp.toLong() * 125613361L shr 27 and 31).toInt()]
    }

    @JvmStatic
    fun log2(value: Int): Int = ceillog2(value) - if (isPowerOfTwo(value)) 0 else 1

    @JvmStatic
    fun isPowerOfTwo(value: Int): Boolean = value != 0 && value and value - 1 == 0

    @JvmStatic
    fun roundUpPow2(value: Int): Int {
        var temp = value - 1
        temp = temp or (temp shr 1)
        temp = temp or (temp shr 2)
        temp = temp or (temp shr 4)
        temp = temp or (temp shr 8)
        temp = temp or (temp shr 16)
        return temp + 1
    }
}