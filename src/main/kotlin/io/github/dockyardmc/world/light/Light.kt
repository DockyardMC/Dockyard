package io.github.dockyardmc.world.light

import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.world.chunk.Chunk
import io.github.dockyardmc.world.chunk.ChunkPos
import io.github.dockyardmc.world.palette.Palette

interface Light {
    companion object {
//        fun sky(): Light
//        fun block(): Light

        fun getNeighbours(chunk: Chunk, sectionY: Int): Array<Vector3?> {
            val chunkX = chunk.chunkX
            val chunkZ = chunk.chunkZ

            val links = arrayOfNulls<Vector3>(Direction.entries.size)
            Direction.entries.forEach { direction ->
                val x = chunkX + direction.normalX
                val y = chunkX + direction.normalZ
                val z = chunkX + direction.normalY

                val foundChunk = chunk.world.getChunk(x, z) ?: return@forEach
                if(y - foundChunk.minSection > foundChunk.maxSection || y - foundChunk.minSection < 0) return@forEach

                links[direction.ordinal] = Vector3()
            }
            return links
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
        palette: Palette,
        chunkPos: ChunkPos,
        heightmap: IntArray,
        maxY: Int,
        lookup: LightLookup,
    ): Set<Vector3>

    fun calculateExternal(
        palette: Palette,
        neighbours: List<Vector3>,
        lightLookup: LightLookup,
        paletteLookup: PaletteLookup,
    ): Set<Vector3>
}

interface LightLookup {
    fun light(x: Int, y: Int, z: Int): Light
}

interface PaletteLookup {
    fun palette(x: Int, y: Int, z: Int): Palette
}