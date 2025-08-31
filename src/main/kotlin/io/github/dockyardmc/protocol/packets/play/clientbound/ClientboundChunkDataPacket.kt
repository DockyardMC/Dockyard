package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.types.writeMap
import io.github.dockyardmc.world.Light
import io.github.dockyardmc.world.block.BlockEntity
import io.github.dockyardmc.world.chunk.ChunkHeightmap
import io.github.dockyardmc.world.chunk.ChunkSection
import io.github.dockyardmc.world.chunk.ChunkUtils
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.objects.ObjectCollection

class ClientboundChunkDataPacket(x: Int, z: Int, heightmaps: Map<ChunkHeightmap.Type, LongArray>, sections: MutableList<ChunkSection>, blockEntities: ObjectCollection<BlockEntity>, light: Light) : ClientboundPacket() {

    init {
        //X Z
        buffer.writeInt(x)
        buffer.writeInt(z)

        //Heightmaps
        buffer.writeMap<ChunkHeightmap.Type, List<Long>>(heightmaps.mapValues { map -> map.value.toList() }, ByteBuf::writeEnum, ByteBuf::writeLongArray)

        //Chunk Sections
        val chunkSectionData = Unpooled.buffer()
        sections.forEach { section ->
            section.write(chunkSectionData)
        }
        buffer.writeByteArray(chunkSectionData.toByteArraySafe())

        //Block Entities
        buffer.writeVarInt(blockEntities.size)
        blockEntities.forEach { blockEntity ->
            val id = blockEntity.blockEntityTypeId
            val point = ChunkUtils.chunkBlockIndexGetGlobal(blockEntity.positionIndex, 0, 0)

            buffer.writeByte(((point.x and 15) shl 4 or (point.z and 15)))
            buffer.writeShort(point.y)
            buffer.writeVarInt(id)
            buffer.writeNBT(blockEntity.data)
        }

        light.write(buffer)
    }
}