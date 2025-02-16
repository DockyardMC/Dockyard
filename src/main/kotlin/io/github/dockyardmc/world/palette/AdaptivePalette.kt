package io.github.dockyardmc.world.palette

import io.github.dockyardmc.utils.bitsToRepresent
import io.github.dockyardmc.world.palette.Palette.EntryConsumer
import io.github.dockyardmc.world.palette.Palette.EntrySupplier
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import java.util.function.IntUnaryOperator

//switches palette type depending on the use case
internal class AdaptivePalette(var dimension: Byte, var maxBitsPerEntry: Byte, bitsPerEntry: Byte) : Palette, Cloneable {
    val defaultBitsPerEntry: Byte
    var palette: SpecializedPalette

    init {
        validateDimension(dimension.toInt())
        defaultBitsPerEntry = bitsPerEntry
        palette = FilledPalette(dimension, 0)
    }

    override fun get(x: Int, y: Int, z: Int): Int {
        require(!(x < 0 || y < 0 || z < 0)) { "Coordinates must be positive" }
        return palette[x, y, z]
    }

    override fun getAll(consumer: EntryConsumer) {
        palette.getAll(consumer)
    }

    override fun getAllPresent(consumer: EntryConsumer) {
        palette.getAllPresent(consumer)
    }

    override fun set(x: Int, y: Int, z: Int, value: Int) {
        require(!(x < 0 || y < 0 || z < 0)) { "Coordinates must be positive" }
        flexiblePalette()[x, y, z] = value
    }

    override fun fill(value: Int) {
        palette = FilledPalette(dimension, value)
    }

    override fun setAll(supplier: EntrySupplier) {
        val newPalette: SpecializedPalette = FlexiblePalette(this)
        newPalette.setAll(supplier)
        palette = newPalette
    }

    override fun replace(x: Int, y: Int, z: Int, operator: IntUnaryOperator) {
        require(!(x < 0 || y < 0 || z < 0)) { "Coordinates must be positive" }
        flexiblePalette().replace(x, y, z, operator)
    }

    override fun replaceAll(function: Palette.EntryFunction) {
        flexiblePalette().replaceAll(function)
    }

    override fun count(): Int = palette.count()

    override fun bitsPerEntry(): Int = palette.bitsPerEntry()

    override fun maxBitsPerEntry(): Int = maxBitsPerEntry.toInt()

    override fun dimension(): Int = dimension.toInt()

    override fun clone(): Palette {
        return try {
            val adaptivePalette = super.clone() as AdaptivePalette
            adaptivePalette.palette = palette.clone()
            adaptivePalette
        } catch (e: CloneNotSupportedException) {
            throw RuntimeException(e)
        }
    }

    fun optimizedPalette(): SpecializedPalette {
        val currentPalette = palette
        if (currentPalette is FlexiblePalette) {
            val count = currentPalette.count()
            if (count == 0) {
                return FilledPalette(dimension, 0)
            }
            // Find all entries and compress the palette
            val entries: IntSet = IntOpenHashSet(currentPalette.paletteToValueList.size)
            currentPalette.getAll { x: Int, y: Int, z: Int, value: Int ->
                entries.add(
                    value
                )
            }
            val currentBitsPerEntry = currentPalette.bitsPerEntry()
            var bitsPerEntry = 0
            if (entries.size == 1) {
                return FilledPalette(dimension, entries.iterator().nextInt())
            } else if (currentBitsPerEntry > defaultBitsPerEntry &&
                bitsToRepresent(entries.size - 1).also { bitsPerEntry = it } < currentBitsPerEntry
            ) {
                currentPalette.resize(bitsPerEntry.toByte())
                return currentPalette
            }
        }
        return currentPalette
    }

    fun flexiblePalette(): Palette {
        var currentPalette = palette
        if (currentPalette is FilledPalette) {
            val filledPalette = currentPalette
            currentPalette = FlexiblePalette(this)
            currentPalette.fill(filledPalette.value)
            palette = currentPalette
        }



        return currentPalette

    }

    companion object {
        private fun validateDimension(dimension: Int) {
            require(!(dimension <= 1 || dimension and dimension - 1 != 0)) { "Dimension must be a positive power of 2" }
        }
    }
}
