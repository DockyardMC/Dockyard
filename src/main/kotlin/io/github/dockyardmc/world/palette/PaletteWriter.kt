package io.github.dockyardmc.world.palette

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeVarIntArray
import io.netty.buffer.ByteBuf

fun ByteBuf.writePalette(palette: Palette) {
    if(palette is AdaptivePalette) {
        val optimized: SpecializedPalette = palette.optimizedPalette()
        palette.palette = optimized
        this.writePalette(optimized)
    }

    if(palette is FilledPalette) {
        this.writeByte(0)
        this.writeVarInt(palette.value)
    }

    if(palette is FlexiblePalette) {
        this.writeByte(palette.bitsPerEntry())
        if(palette.bitsPerEntry() <= palette.maxBitsPerEntry()) {
            this.writeVarIntArray(palette.paletteToValueList.toList())
        }
//        this.writeLongArray(palette.values.toList())
        palette.values.forEach { value ->
            this.writeLong(value)
        }
    }
}
