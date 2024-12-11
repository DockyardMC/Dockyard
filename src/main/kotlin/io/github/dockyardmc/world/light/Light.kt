package io.github.dockyardmc.world.light

import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.Vector3d
import io.github.dockyardmc.world.chunk.Chunk
import io.github.dockyardmc.world.chunk.ChunkPos
import io.github.dockyardmc.world.palette.Palette

interface Light {
    companion object {

        fun getNeighbours(chunk: Chunk, sectionY: Int): Array<Vector3d> {
            val chunkX = chunk.chunkX
            val chunkZ = chunk.chunkZ

            val links = arrayOfNulls<Vector3d>(Direction.entries.size)
            Direction.entries.forEach { direction ->
                val x = chunkX + direction.normalX
                val y = sectionY + direction.normalY
                val z = chunkZ + direction.normalZ

                val foundChunk = chunk.world.getChunk(x, z) ?: return@forEach
                if(y - foundChunk.minSection > foundChunk.maxSection || y - foundChunk.minSection < 0) return@forEach

                links[direction.ordinal] = Vector3d(foundChunk.chunkX.toDouble(), y.toDouble(), foundChunk.chunkZ.toDouble())
            }
            return links.requireNoNulls()
        }
    }

    val requiresSend: Boolean
    val byteArray: ByteArray

    fun flip()

    fun getLevel(x: Int, y: Int, z: Int): Int

    fun invalidate()

    fun requiresUpdate(): Boolean

    fun set(copyArray: ByteArray)

    fun calculateInternal(
        blockPalette: Palette,
        chunkPos: ChunkPos,
        chunkY: Int,
        heightmap: IntArray,
        maxY: Int,
        lookup: LightLookup,
    ): Set<Vector3d>

    fun calculateExternal(
        blockPalette: Palette,
        neighbours: List<Vector3d>,
        lightLookup: LightLookup,
        paletteLookup: PaletteLookup,
    ): Set<Vector3d>
}

@FunctionalInterface
fun interface LightLookup {
    fun light(x: Int, y: Int, z: Int): Light?
}

@FunctionalInterface
fun interface PaletteLookup {
    fun palette(x: Int, y: Int, z: Int): Palette?
}