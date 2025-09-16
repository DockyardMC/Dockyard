package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.codec.ExtraCodecs
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
            StreamCodec.enum<ChunkHeightmap.Type>().mapTo(ExtraCodecs.LongArray.STREAM), ClientboundChunkDataPacket::heightmaps,
            ChunkSection.BYTE_ARRAY_STREAM_CODEC, ClientboundChunkDataPacket::sections,
            BlockEntity.STREAM_CODEC.list(), ClientboundChunkDataPacket::blockEntities,
            LightData.STREAM_CODEC, ClientboundChunkDataPacket::light,
            ::ClientboundChunkDataPacket
        )
    }

    init {
        STREAM_CODEC.write(buffer, this)
    }

//    init {
//        //X Z
//        buffer.writeInt(x)
//        buffer.writeInt(z)
//
//        //Heightmaps
//        buffer.writeMap<ChunkHeightmap.Type, List<Long>>(heightmaps.mapValues { map -> map.value.toList() }, ByteBuf::writeEnum, ByteBuf::writeLongArray)
//
//        //Chunk Sections
//        val chunkSectionData = Unpooled.buffer()
//        sections.forEach { section ->
//            section.write(chunkSectionData)
//        }
//        buffer.writeByteArray(chunkSectionData.toByteArraySafe())
//
//        //Block Entities
//        buffer.writeVarInt(blockEntities.size)
//        blockEntities.forEach { blockEntity ->
//            val id = blockEntity.blockEntityTypeId
//            val point = ChunkUtils.chunkBlockIndexGetGlobal(blockEntity.positionIndex, 0, 0)
//
//            buffer.writeByte(((point.x and 15) shl 4 or (point.z and 15)))
//            buffer.writeShort(point.y)
//            buffer.writeVarInt(id)
//            buffer.writeNBT(blockEntity.data)
//        }
//
//        light.write(buffer)
//    }
}