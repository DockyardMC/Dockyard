package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.utils.writeMSNBT
import io.github.dockyardmc.world.ChunkSection
import io.github.dockyardmc.world.Light
import io.github.dockyardmc.world.writeChunkSection
import io.netty.buffer.Unpooled
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class ClientboundChunkDataPacket(x: Int, z: Int, heightMap: NBTCompound, sections: MutableList<ChunkSection>, light: Light): ClientboundPacket(0x25) {

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
        data.writeVarInt(0)

        // Light stuff
        data.writeLongArray(light.skyMask.toLongArray())
        data.writeLongArray(light.blockMask.toLongArray())

        data.writeLongArray(light.emptySkyMask.toLongArray())
        data.writeLongArray(light.emptyBlockMask.toLongArray())

        data.writeByteArray(light.skyLight)
        data.writeByteArray(light.blockLight)
    }
}