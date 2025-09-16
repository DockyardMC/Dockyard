package io.github.dockyardmc.world.block

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.world.chunk.ChunkUtils
import io.netty.buffer.ByteBuf
import net.kyori.adventure.nbt.CompoundBinaryTag

data class BlockEntity(
    val positionIndex: Int,
    val block: RegistryBlock,
    val data: CompoundBinaryTag,
) {
    val blockEntityTypeId get() = block.blockEntityId!!

    companion object {
        val STREAM_CODEC = object : StreamCodec<BlockEntity> {

            override fun write(buffer: ByteBuf, value: BlockEntity) {
                val id = value.blockEntityTypeId
                val point = ChunkUtils.chunkBlockIndexGetGlobal(value.positionIndex, 0, 0)

                buffer.writeByte(((point.x and 15) shl 4 or (point.z and 15)))
                buffer.writeShort(point.y)
                buffer.writeVarInt(id)
                buffer.writeNBT(value.data)

            }

            override fun read(buffer: ByteBuf): BlockEntity {
                throw UnsupportedOperationException()
            }
        }
    }
}