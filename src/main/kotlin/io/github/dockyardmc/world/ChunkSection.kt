package io.github.dockyardmc.world

import io.github.dockyardmc.world.palette.Palette
import io.github.dockyardmc.world.palette.writePalette
import io.netty.buffer.ByteBuf

class ChunkSection(
    var blockPalette: Palette,
    var biomePalette: Palette,

) {
    companion object {
        fun empty(): ChunkSection {
            return ChunkSection(Palette.blocks(), Palette.biomes())
        }
    }

    fun clear() {
        blockPalette.fill(0)
        biomePalette.fill(0)
    }
}

fun ByteBuf.writeSection(section: ChunkSection) {
    this.writeShort(section.blockPalette.count())
    this.writePalette(section.blockPalette)
    this.writePalette(section.biomePalette)
}