package io.github.dockyardmc.world.palette

import io.github.dockyardmc.maths.bitsToRepresent
import io.github.dockyardmc.world.palette.Palette.EntryConsumer
import io.github.dockyardmc.world.palette.Palette.EntrySupplier
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.IntArrayList
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.IntUnaryOperator
import kotlin.math.min

//Palette able to take any value anywhere. May use more memory than required.
internal class FlexiblePalette @JvmOverloads constructor(// Specific to this palette type
    private val adaptivePalette: AdaptivePalette, private var bitsPerEntry: Byte = adaptivePalette.defaultBitsPerEntry
) :
    SpecializedPalette, Cloneable {
    private var count = 0
    var values: LongArray

    var paletteToValueList: IntArrayList

    private var valueToPaletteMap: Int2IntOpenHashMap

    init {
        paletteToValueList = IntArrayList(1)
        paletteToValueList.add(0)
        valueToPaletteMap = Int2IntOpenHashMap(1)
        valueToPaletteMap.put(0, 0)
        valueToPaletteMap.defaultReturnValue(-1)
        val valuesPerLong = 64 / bitsPerEntry
        values = LongArray((maxSize() + valuesPerLong - 1) / valuesPerLong)
    }

    override fun get(x: Int, y: Int, z: Int): Int {
        val bitsPerEntry = bitsPerEntry.toInt()
        val sectionIndex = getSectionIndex(dimension(), x, y, z)
        val valuesPerLong = 64 / bitsPerEntry
        val index = sectionIndex / valuesPerLong
        val bitIndex = (sectionIndex - index * valuesPerLong) * bitsPerEntry
        val value = (values[index] shr bitIndex).toInt() and (1 shl bitsPerEntry) - 1
        return if (hasPalette()) paletteToValueList.getInt(value) else value
    }

    override fun getAll(consumer: EntryConsumer) {
        retrieveAll(consumer, true)
    }

    override fun getAllPresent(consumer: EntryConsumer) {
        retrieveAll(consumer, false)
    }

    override fun set(x: Int, y: Int, z: Int, value: Int) {
        var modValue = value
        modValue = getPaletteIndex(modValue)
        val bitsPerEntry = bitsPerEntry.toInt()
        val values = values
        val valuesPerLong = 64 / bitsPerEntry
        val sectionIndex = getSectionIndex(dimension(), x, y, z)
        val index = sectionIndex / valuesPerLong
        val bitIndex = (sectionIndex - index * valuesPerLong) * bitsPerEntry
        val block = values[index]
        val clear = (1L shl bitsPerEntry) - 1L
        val oldBlock = block shr bitIndex and clear
        values[index] = block and (clear shl bitIndex).inv() or (modValue.toLong() shl bitIndex)
        val currentAir = oldBlock == 0L
        if (currentAir != (modValue == 0)) count += if (currentAir) 1 else -1
    }

    override fun fill(value: Int) {
        var modVal = value
        if (modVal == 0) {
            Arrays.fill(values, 0)
            count = 0
            return
        }
        modVal = getPaletteIndex(modVal)
        val bitsPerEntry = bitsPerEntry.toInt()
        val valuesPerLong = 64 / bitsPerEntry
        val values = values
        var block: Long = 0
        for (i in 0 until valuesPerLong) block = block or (modVal.toLong() shl i * bitsPerEntry)
        Arrays.fill(values, block)
        count = maxSize()
    }

    override fun setAll(supplier: EntrySupplier) {
        val cache = WRITE_CACHE.get()
        val dimension = dimension()
        // Fill cache with values
        var fillValue = -1
        var count = 0
        var index = 0
        for (y in 0..<dimension) {
            for (z in 0..<dimension) {
                for (x in 0..<dimension) {
                    var value = supplier[x, y, z]
                    if (fillValue != -2) {
                        if (fillValue == -1) {
                            fillValue = value
                        } else if (fillValue != value) {
                            fillValue = -2
                        }
                    }
                    if (value != 0) {
                        value = getPaletteIndex(value)
                        count++
                    }
                    cache[index++] = value
                }
            }
        }
        assert(index == maxSize())
        if (fillValue < 0) {
            updateAll(cache)
            this.count = count
        } else {
            fill(fillValue)
        }
    }

    override fun replace(x: Int, y: Int, z: Int, operator: IntUnaryOperator) {
        val oldValue = get(x, y, z)
        val newValue = operator.applyAsInt(oldValue)
        if (oldValue != newValue) set(x, y, z, newValue)
    }

    override fun replaceAll(function: Palette.EntryFunction) {
        val cache = WRITE_CACHE.get()
        val arrayIndex = AtomicInteger()
        val count = AtomicInteger()
        getAll { x: Int, y: Int, z: Int, value: Int ->
            val newValue = function.apply(x, y, z, value)
            val index = arrayIndex.getPlain()
            arrayIndex.plain = index + 1
            cache[index] = if (newValue != value) getPaletteIndex(newValue) else value
            if (newValue != 0) count.plain = count.getPlain() + 1
        }
        assert(arrayIndex.getPlain() == maxSize())
        updateAll(cache)
        this.count = count.getPlain()
    }

    override fun count(): Int = count

    override fun bitsPerEntry(): Int = bitsPerEntry.toInt()

    override fun maxBitsPerEntry(): Int = adaptivePalette.maxBitsPerEntry()

    override fun dimension(): Int = adaptivePalette.dimension()

    override fun clone(): SpecializedPalette {
        val palette = super.clone() as FlexiblePalette

        palette.values = values.clone()
        palette.paletteToValueList = paletteToValueList.clone()
        palette.valueToPaletteMap = valueToPaletteMap.clone()
        palette.count = count

        return palette
    }

    private fun retrieveAll(consumer: EntryConsumer, consumeEmpty: Boolean) {
        if (!consumeEmpty && count == 0) return
        val values = values
        val dimension = dimension()
        val bitsPerEntry = bitsPerEntry.toInt()
        val magicMask = (1 shl bitsPerEntry) - 1
        val valuesPerLong = 64 / bitsPerEntry
        val size = maxSize()
        val dimensionMinus = dimension - 1
        val ids = if (hasPalette()) paletteToValueList.elements() else null
        val dimensionBitCount = bitsToRepresent(dimensionMinus)
        val shiftedDimensionBitCount = dimensionBitCount shl 1
        for (i in values.indices) {
            val value = values[i]
            val startIndex = i * valuesPerLong
            val endIndex = min(startIndex + valuesPerLong, size)
            for (index in startIndex..<endIndex) {
                val bitIndex = (index - startIndex) * bitsPerEntry
                val paletteIndex = (value shr bitIndex and magicMask.toLong()).toInt()
                if (consumeEmpty || paletteIndex != 0) {
                    val y = index shr shiftedDimensionBitCount
                    val z = index shr dimensionBitCount and dimensionMinus
                    val x = index and dimensionMinus
                    val result = if (ids != null && paletteIndex < ids.size) ids[paletteIndex] else paletteIndex
                    consumer.accept(x, y, z, result)
                }
            }
        }
    }

    private fun updateAll(paletteValues: IntArray) {
        val size = maxSize()
        assert(paletteValues.size >= size)
        val bitsPerEntry = bitsPerEntry.toInt()
        val valuesPerLong = 64 / bitsPerEntry
        val clear = (1L shl bitsPerEntry) - 1L
        val values = values
        for (i in values.indices) {
            var block = values[i]
            val startIndex = i * valuesPerLong
            val endIndex = min(startIndex + valuesPerLong, size)
            for (index in startIndex until endIndex) {
                val bitIndex = (index - startIndex) * bitsPerEntry
                block = block and (clear shl bitIndex).inv() or (paletteValues[index].toLong() shl bitIndex)
            }
            values[i] = block
        }
    }

    fun resize(newBitsPerEntry: Byte) {
        var newBitsPerEntryMod = newBitsPerEntry
        newBitsPerEntryMod = if (newBitsPerEntryMod > maxBitsPerEntry()) 15 else newBitsPerEntryMod
        val palette = FlexiblePalette(
            adaptivePalette, newBitsPerEntryMod
        )
        palette.paletteToValueList = paletteToValueList
        palette.valueToPaletteMap = valueToPaletteMap
        getAll { x: Int, y: Int, z: Int, value: Int ->
            palette[x, y, z] = value
        }
        bitsPerEntry = palette.bitsPerEntry
        values = palette.values
        assert(count == palette.count)
    }

    private fun getPaletteIndex(value: Int): Int {
        if (!hasPalette()) return value
        val lastPaletteIndex = paletteToValueList.size
        val bpe = bitsPerEntry
        if (lastPaletteIndex >= maxPaletteSize(bpe.toInt())) {
            // resize if full
            resize((bpe + 1).toByte())
            return getPaletteIndex(value)
        }
        val lookup = valueToPaletteMap.putIfAbsent(value, lastPaletteIndex)
        if (lookup != -1) return lookup
        paletteToValueList.add(value)
        assert(lastPaletteIndex < maxPaletteSize(bpe.toInt()))
        return lastPaletteIndex
    }

    fun hasPalette(): Boolean = bitsPerEntry <= maxBitsPerEntry()

    companion object {
        private val WRITE_CACHE = ThreadLocal.withInitial { IntArray(4096) }
        fun getSectionIndex(dimension: Int, x: Int, y: Int, z: Int): Int {
            val dimensionMask = dimension - 1
            val dimensionBitCount = bitsToRepresent(dimensionMask)
            return y and dimensionMask shl (dimensionBitCount shl 1) or (
                    z and dimensionMask shl dimensionBitCount) or
                    (x and dimensionMask)
        }

        fun maxPaletteSize(bitsPerEntry: Int): Int = 1 shl bitsPerEntry
    }
}
