package io.github.dockyardmc.world.chunk

import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.world.light.BlockLight
import io.github.dockyardmc.world.light.SkyLight
import io.github.dockyardmc.world.palette.Palette
import io.github.dockyardmc.world.palette.writePalette
import io.netty.buffer.ByteBuf

class ChunkSection(
    var blockPalette: Palette,
    var biomePalette: Palette,
    var skyLight: SkyLight,
    var blockLight: BlockLight
) {
    companion object {
        fun empty(): ChunkSection {
            val defaultBlocks = Palette.blocks()
            val defaultBiomes = Palette.biomes()
            defaultBlocks.fill(Blocks.AIR.defaultBlockStateId)
            defaultBiomes.fill(Biomes.THE_VOID.getProtocolId())
            return ChunkSection(defaultBlocks, defaultBiomes, SkyLight(), BlockLight())
        }
    }
}

fun ByteBuf.writeChunkSection(section: ChunkSection) {
    this.writeShort(section.blockPalette.count())
    this.writePalette(section.blockPalette)
    this.writePalette(section.biomePalette)
}