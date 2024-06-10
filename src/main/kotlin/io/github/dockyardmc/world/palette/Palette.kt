package io.github.dockyardmc.world.palette

import java.util.function.IntUnaryOperator

interface Palette {
    operator fun get(x: Int, y: Int, z: Int): Int
    fun getAll(consumer: EntryConsumer)
    fun getAllPresent(consumer: EntryConsumer)
    operator fun set(x: Int, y: Int, z: Int, value: Int)
    fun fill(value: Int)
    fun setAll(supplier: EntrySupplier)
    fun replace(x: Int, y: Int, z: Int, operator: IntUnaryOperator)
    fun replaceAll(function: EntryFunction)

    fun count(): Int

    fun bitsPerEntry(): Int
    fun maxBitsPerEntry(): Int
    fun dimension(): Int

    fun maxSize(): Int {
        val dimension = dimension()
        return dimension * dimension * dimension
    }

    fun clone(): Palette

    fun interface EntrySupplier {
        operator fun get(x: Int, y: Int, z: Int): Int
    }

    fun interface EntryConsumer {
        fun accept(x: Int, y: Int, z: Int, value: Int)
    }

    fun interface EntryFunction {
        fun apply(x: Int, y: Int, z: Int, value: Int): Int
    }

    companion object {
        fun blocks(): Palette {
            return newPalette(16, 8, 4)
        }

        fun biomes(): Palette {
            return newPalette(4, 3, 1)
        }

        fun newPalette(dimension: Int, maxBitsPerEntry: Int, bitsPerEntry: Int): Palette {
            return AdaptivePalette(dimension.toByte(), maxBitsPerEntry.toByte(), bitsPerEntry.toByte())
        }
    }
}
