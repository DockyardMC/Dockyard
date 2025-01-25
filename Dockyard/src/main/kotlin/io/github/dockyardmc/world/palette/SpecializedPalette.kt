package io.github.dockyardmc.world.palette

import io.github.dockyardmc.world.palette.Palette.EntrySupplier
import java.util.function.IntUnaryOperator

internal interface SpecializedPalette : Palette {
    override fun bitsPerEntry(): Int {
        throw UnsupportedOperationException()
    }

    override fun maxBitsPerEntry(): Int {
        throw UnsupportedOperationException()
    }

    override fun clone(): SpecializedPalette
    interface Immutable : SpecializedPalette {
        override fun set(x: Int, y: Int, z: Int, value: Int) {
            throw UnsupportedOperationException()
        }

        override fun fill(value: Int) {
            throw UnsupportedOperationException()
        }

        override fun setAll(supplier: EntrySupplier) {
            throw UnsupportedOperationException()
        }

        override fun replace(x: Int, y: Int, z: Int, operator: IntUnaryOperator) {
            throw UnsupportedOperationException()
        }

        override fun replaceAll(function: Palette.EntryFunction) {
            throw UnsupportedOperationException()
        }
    }
}
