package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.chunk.ChunkUtils
import io.github.dockyardmc.world.Light
import io.github.dockyardmc.world.block.BlockEntity
import io.github.dockyardmc.world.chunk.ChunkSection
import io.github.dockyardmc.world.chunk.Heightmap
import io.github.dockyardmc.world.chunk.writeChunkSection
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.objects.ObjectCollection

class ClientboundChunkDataPacket(x: Int, z: Int, heightmaps: Map<Heightmap.Type, List<Long>>, sections: MutableList<ChunkSection>, blockEntities: ObjectCollection<BlockEntity>, light: Light) : ClientboundPacket() {

    init {
        //X Z
        buffer.writeInt(x)
        buffer.writeInt(z)

        //Heightmaps
        //TODO make method to write maps
        buffer.writeVarInt(heightmaps.size)
        heightmaps.forEach { (key, value) ->
            buffer.writeVarIntEnum(key)
            buffer.writeLongArray(value)
        }

        //Chunk Sections
        val chunkSectionData = Unpooled.buffer()
        sections.forEach(chunkSectionData::writeChunkSection)
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

        // Light stuff
        buffer.writeLongArray(light.skyMask.toLongArray())
        buffer.writeLongArray(light.blockMask.toLongArray())

        buffer.writeLongArray(light.emptySkyMask.toLongArray())
        buffer.writeLongArray(light.emptyBlockMask.toLongArray())

        buffer.writeByteArray(light.skyLight)
        buffer.writeByteArray(light.blockLight)
    }
}