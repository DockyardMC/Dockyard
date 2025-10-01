package io.github.dockyardmc.world.chunk

import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.world.palette.Palette
import io.github.dockyardmc.world.palette.writePalette
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

data class ChunkSection(
    private var blockPalette: Palette,
    private var biomePalette: Palette,
) {

    var nonEmptyBlockCount: Int = 0

    init {
        recountNonEmptyBlocks()
    }

    fun fillBiome(biome: Int) {
        biomePalette.fill(biome)
    }

    fun fillBlock(block: Int) {
        blockPalette.fill(block)
        recountNonEmptyBlocks()
    }

    fun getBlock(x: Int, y: Int, z: Int): Int {
        return blockPalette[x, y, z]
    }

    fun getBiome(x: Int, y: Int, z: Int): Int {
        return biomePalette[x, y, z]
    }

    fun setBlock(x: Int, y: Int, z: Int, block: Int) {
        val old = getAndSetBlock(x, y, z, block)
        if (old != Blocks.AIR.defaultBlockStateId) nonEmptyBlockCount--
        if (block != Blocks.AIR.defaultBlockStateId) nonEmptyBlockCount++
    }

    fun setBiome(x: Int, y: Int, z: Int, biome: Int) {
        biomePalette[x, y, z] = biome
    }

    fun hasOnlyAir(): Boolean = nonEmptyBlockCount == 0

    fun getAndSetBlock(x: Int, y: Int, z: Int, block: Int): Int {
        val id = blockPalette[x, y, z]
        blockPalette[x, y, z] = block
        return id
    }

    private fun recountNonEmptyBlocks() {
        nonEmptyBlockCount = 0
        for (y in 0..<16) for (z in 0..<16) for (x in 0..<16) {
            if (blockPalette[x, y, z] != 0) nonEmptyBlockCount++
        }
    }

    companion object {

        val BYTE_ARRAY_STREAM_CODEC = StreamCodec.BYTE_ARRAY.transform<List<ChunkSection>>(
            { from ->
                val innerBuffer = Unpooled.buffer()
                from.forEach { section ->
                    STREAM_CODEC.write(innerBuffer, section)
                }
                innerBuffer
            },
            { _ ->
                throw UnsupportedOperationException()
            }
        )

        val STREAM_CODEC = object : StreamCodec<ChunkSection> {

            override fun write(buffer: ByteBuf, value: ChunkSection) {
                buffer.writeShort(value.blockPalette.count())
                buffer.writePalette(value.blockPalette)
                buffer.writePalette(value.biomePalette)
            }

            override fun read(buffer: ByteBuf): ChunkSection {
                throw UnsupportedOperationException()
            }
        }

        fun empty(): ChunkSection {
            val defaultBlocks = Palette.blocks()
            val defaultBiomes = Palette.biomes()
            defaultBlocks.fill(Blocks.AIR.defaultBlockStateId)
            defaultBiomes.fill(Biomes.THE_VOID.getProtocolId())
            return ChunkSection(defaultBlocks, defaultBiomes)
        }
    }
}