package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.toByteArraySafe
import io.github.dockyardmc.extentions.writeByteArray
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.writeMSNBT
import io.github.dockyardmc.world.Light
import io.github.dockyardmc.world.block.BlockEntity
import io.github.dockyardmc.world.chunk.ChunkSection
import io.github.dockyardmc.world.chunk.ChunkUtils
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.objects.ObjectCollection
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class ClientboundChunkDataPacket(x: Int, z: Int, heightMap: NBTCompound, sections: MutableList<ChunkSection>, blockEntities: ObjectCollection<BlockEntity>, light: Light) : ClientboundPacket() {
    init {
        //X Z
        buffer.writeInt(x)
        buffer.writeInt(z)

        //Heightmaps
        buffer.writeMSNBT(heightMap)

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