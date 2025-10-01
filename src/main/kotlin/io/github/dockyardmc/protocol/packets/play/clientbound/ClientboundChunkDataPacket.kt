package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.world.LightData
import io.github.dockyardmc.world.block.BlockEntity
import io.github.dockyardmc.world.chunk.ChunkHeightmap
import io.github.dockyardmc.world.chunk.ChunkSection

data class ClientboundChunkDataPacket(val x: Int, val z: Int, val heightmaps: Map<ChunkHeightmap.Type, LongArray>, val sections: List<ChunkSection>, val blockEntities: List<BlockEntity>, val light: LightData) : ClientboundPacket() {

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.INT, ClientboundChunkDataPacket::x,
            StreamCodec.INT, ClientboundChunkDataPacket::z,
            StreamCodec.enum<ChunkHeightmap.Type>().mapTo(StreamCodec.LONG_ARRAY), ClientboundChunkDataPacket::heightmaps,
            ChunkSection.BYTE_ARRAY_STREAM_CODEC, ClientboundChunkDataPacket::sections,
            BlockEntity.STREAM_CODEC.list(), ClientboundChunkDataPacket::blockEntities,
            LightData.STREAM_CODEC, ClientboundChunkDataPacket::light,
            ::ClientboundChunkDataPacket
        )
    }

    init {
        STREAM_CODEC.write(buffer, this)
    }
}