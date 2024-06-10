package io.github.dockyardmc.utils

import kotlin.math.floor

object ChunkUtils {

    fun getChunkCoordinate(xz: Int): Int {
        return xz shr 4
    }

    fun sectionRelative(xyz: Int): Int {
        return xyz and 0xF;
    }

    fun getChunkIndex(x: Int, z: Int): Long {
        return x.toLong() shl 32 or (z.toLong() and 0xffffffffL)
    }

    fun getChunkCoordinate(xz: Double): Int {
        return getChunkCoordinate(floor(xz).toInt())
    }
}