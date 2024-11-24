package io.github.dockyardmc.world

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.utils.ChunkUtils

data class ChunkPos(val x: Int, val z: Int) {

    fun pack(): Long {
        return pack(x, z)
    }

    companion object {

        val ZERO = ChunkPos(0, 0)

        fun pack(x: Int, z: Int): Long = x.toLong() and 0xFFFFFFFFL or (z.toLong() and 0xFFFFFFFFL shl 32)

        fun unpackX(encoded: Long): Int = (encoded and 0xFFFFFFFFL).toInt()

        fun unpackZ(encoded: Long): Int = (encoded ushr 32 and 0xFFFFFFFFL).toInt()

        fun unpack(encoded: Long): Pair<Int, Int> {
            return unpackX(encoded) to unpackZ(encoded)
        }

        fun fromIndex(encoded: Long): ChunkPos {
            val (x, z) = unpackX(encoded) to unpackZ(encoded)
            return ChunkPos(x, z)
        }

        fun fromLocation(location: Location): ChunkPos {
            val x = ChunkUtils.getChunkCoordinate(location.x)
            val z = ChunkUtils.getChunkCoordinate(location.z)

            return ChunkPos(x, z)
        }
    }
}