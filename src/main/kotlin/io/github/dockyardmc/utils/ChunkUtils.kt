package io.github.dockyardmc.utils

import io.github.dockyardmc.world.Chunk
import kotlin.math.floor

object ChunkUtils {

    fun getChunkCoordinate(xz: Int): Int = xz shr 4

    fun sectionRelative(xyz: Int): Int = xyz and 0xF

    fun getChunkIndex(chunk: Chunk): Long = getChunkIndex(chunk.chunkX, chunk.chunkZ)

    fun getChunkCoordinate(xz: Double): Int = getChunkCoordinate(floor(xz).toInt())

    fun getChunkIndex(chunkX: Int, chunkZ: Int): Long = ((chunkX.toLong()) shl 32) or (chunkZ.toLong() and 0xffffffffL)

    fun getChunkX(index: Long): Int = (index shr 32).toInt()

    fun getChunkZ(index: Long): Int = index.toInt()

    fun getChunkCoordsFromIndex(index: Long): Pair<Int, Int> = getChunkX(index) to getChunkZ(index)
}