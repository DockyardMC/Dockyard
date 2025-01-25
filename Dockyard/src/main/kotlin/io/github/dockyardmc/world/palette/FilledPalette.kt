package io.github.dockyardmc.world.palette

import io.github.dockyardmc.world.palette.Palette.EntryConsumer


 //Palette containing a single value. Useful for both empty and full palettes.
internal data class FilledPalette(val dim: Byte, val value: Int) : SpecializedPalette.Immutable {
    override fun get(x: Int, y: Int, z: Int): Int = value

    override fun getAll(consumer: EntryConsumer) {
        val dimension = dim
        val value = value
        for (y in 0..<dimension) for (z in 0..<dimension) for (x in 0..<dimension) consumer.accept(
            x,
            y, z, value
        )
    }

    override fun getAllPresent(consumer: EntryConsumer) {
        if (value != 0) getAll(consumer)
    }

    override fun count(): Int = if (value != 0) maxSize() else 0

    override fun dimension(): Int = dim.toInt()

    override fun clone(): SpecializedPalette = this
}
