package io.github.dockyardmc.utils

import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.world.chunk.Chunk
import io.github.dockyardmc.world.chunk.ChunkPos
import kotlin.math.abs
import kotlin.math.sin

object ChunkUtils {

    private val MULTIPLY_DE_BRUIJN_BIT_POSITION = intArrayOf(
        0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8,
        31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
    )
    private val SIN = FloatArray(65536) { sin(it.toDouble() * Math.PI * 2.0 / 65536.0).toFloat() }
    const val EPSILON: Float = 1.0E-5F
    const val TO_RADIANS_FACTOR = Math.PI.toFloat() / 180F

    fun chunkBlockIndex(x: Int, y: Int, z: Int): Int {
        var xCoord = x
        var zCoord = z
        xCoord = globalToSectionRelative(xCoord)
        zCoord = globalToSectionRelative(zCoord)

        var index = xCoord and 0xF // 4 bits
        if (y > 0) {
            index = index or ((y shl 4) and 0x07FFFFF0) // 23 bits (24th bit is always 0 because y is positive)
        } else {
            index = index or (((-y) shl 4) and 0x7FFFFF0) // Make positive and use 23 bits
            index = index or (1 shl 27) // Set negative sign at 24th bit
        }
        index = index or ((zCoord shl 28) and -0x10000000) // 4 bits
        return index
    }

    fun chunkBlockIndexGetX(index: Int): Int {
        return index and 0xF // 0-4 bits
    }

    fun chunkBlockIndexGetY(index: Int): Int {
        var y = (index and 0x07FFFFF0) ushr 4
        if (((index ushr 27) and 1) == 1) y = -y // Sign bit set, invert sign

        return y // 4-28 bits
    }

    fun chunkBlockIndexGetZ(index: Int): Int {
        return (index shr 28) and 0xF // 28-32 bits
    }

    fun chunkBlockIndexGetGlobal(index: Int, chunkX: Int, chunkZ: Int): Vector3 {
        val x: Int = chunkBlockIndexGetX(index) + 16 * chunkX
        val y: Int = chunkBlockIndexGetY(index)
        val z: Int = chunkBlockIndexGetZ(index) + 16 * chunkZ
        return Vector3(x, y, z)
    }

    fun globalToSectionRelative(xyz: Int): Int {
        return xyz and 0xF
    }

    fun getChunkCoordinate(xz: Int): Int = xz shr 4

    fun sectionRelative(xyz: Int): Int = xyz and 0xF

    fun getChunkIndex(chunk: Chunk): Long = getChunkIndex(chunk.chunkX, chunk.chunkZ)

    fun getChunkCoordinate(xz: Double): Int = getChunkCoordinate(floor(xz).toInt())

    fun getChunkIndex(chunkX: Int, chunkZ: Int): Long = ((chunkX.toLong()) shl 32) or (chunkZ.toLong() and 0xffffffffL)

    fun getChunkX(index: Long): Int = (index shr 32).toInt()

    fun getChunkZ(index: Long): Int = index.toInt()

    fun getChunkCoordsFromIndex(index: Long): Pair<Int, Int> = getChunkX(index) to getChunkZ(index)

    fun forDifferingChunksInRange(chunkX: Int, chunkZ: Int, oldChunkX: Int, oldChunkZ: Int, range: Int): List<ChunkPos> {
        val list = mutableListOf<ChunkPos>()
        for (x in chunkX - range..chunkX + range) {
            for (z in chunkZ - range..chunkZ + range) {
                // If the difference between either the x and old x or z and old z is > range, then the chunk is
                // newly in range, and we can process it.
                if (abs(x - oldChunkX) > range || abs(z - oldChunkZ) > range) list.add(ChunkPos(x, z))
            }
        }
        return list
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