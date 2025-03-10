package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.ChunkUtils
import io.github.dockyardmc.utils.writeMSNBT
import io.github.dockyardmc.world.Light
import io.github.dockyardmc.world.block.BlockEntity
import io.github.dockyardmc.world.chunk.ChunkSection
import io.github.dockyardmc.world.chunk.writeChunkSection
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.objects.ObjectCollection
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class ClientboundChunkDataPacket(x: Int, z: Int, heightMap: NBTCompound, sections: MutableList<ChunkSection>, blockEntities: ObjectCollection<BlockEntity>, light: Light) : ClientboundPacket() {

    init {
        //X Z
        data.writeInt(x)
        data.writeInt(z)

        //Heightmaps
        data.writeMSNBT(heightMap)

        //Chunk Sections
        val chunkSectionData = Unpooled.buffer()
        sections.forEach(chunkSectionData::writeChunkSection)
        data.writeByteArray(chunkSectionData.toByteArraySafe())

        //Block Entities
        data.writeVarInt(blockEntities.size)
        blockEntities.forEach { blockEntity ->
            val id = blockEntity.blockEntityTypeId
            val point = ChunkUtils.chunkBlockIndexGetGlobal(blockEntity.positionIndex, 0, 0)

            data.writeByte(((point.x and 15) shl 4 or (point.z and 15)))
            data.writeShort(point.y)
            data.writeVarInt(id)
            data.writeNBT(blockEntity.data)
        }

        // Light stuff
        data.writeLongArray(light.skyMask.toLongArray())
        data.writeLongArray(light.blockMask.toLongArray())

        data.writeLongArray(light.emptySkyMask.toLongArray())
        data.writeLongArray(light.emptyBlockMask.toLongArray())

        data.writeByteArray(light.skyLight)
        data.writeByteArray(light.blockLight)
    }
}