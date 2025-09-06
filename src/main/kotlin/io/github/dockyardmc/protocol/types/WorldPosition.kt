package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.codec.LocationCodecs
import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

data class WorldPosition(val dimension: String, val blockPosition: Vector3) : NetworkWritable, DataComponentHashable {

    companion object : NetworkReadable<WorldPosition> {

        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.STRING, WorldPosition::dimension,
            LocationCodecs.BLOCK_POSITION, WorldPosition::blockPosition,
            ::WorldPosition
        )

        val CODEC = StructCodec.of(
            "dimension", Codec.STRING, WorldPosition::dimension,
            "pos", Vector3.CODEC, WorldPosition::blockPosition,
            ::WorldPosition
        )

        override fun read(buffer: ByteBuf): WorldPosition {
            return STREAM_CODEC.read(buffer)
        }
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("dimension", CRC32CHasher.ofString(dimension))
            static("pos", CRC32CHasher.ofIntArray(listOf(blockPosition.x, blockPosition.y, blockPosition.z).toIntArray()))
        }
    }

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }

}