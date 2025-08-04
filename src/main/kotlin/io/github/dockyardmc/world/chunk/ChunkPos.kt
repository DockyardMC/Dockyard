package io.github.dockyardmc.world.chunk

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.netty.buffer.ByteBuf

data class ChunkPos(val x: Int, val z: Int) : NetworkWritable {

    fun pack(): Long {
        return pack(x, z)
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(x)
        buffer.writeVarInt(z)
    }

    override fun toString(): String {
        return "[$x, $z]"
    }

    companion object : NetworkReadable<ChunkPos> {

        override fun read(buffer: ByteBuf): ChunkPos {
            return ChunkPos(buffer.readVarInt(), buffer.readVarInt())
        }

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

            return ChunkPos(z, x)
        }
    }
}