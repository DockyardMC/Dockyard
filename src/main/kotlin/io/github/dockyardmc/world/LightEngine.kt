package io.github.dockyardmc.world

import io.github.dockyardmc.world.chunk.Chunk
import io.github.dockyardmc.world.chunk.ChunkSection

class LightEngine {

    private val dimension = 16

    companion object {
        const val ARRAY_SIZE = 16 * 16 * 16 / (8 / 4) // blocks / bytes per block
    }

    private val fullbright: Byte = 15 // 14
    private val half: Byte = 3 // 10
    private val dark: Byte = 2 // 7
    lateinit var recalcArray: ByteArray

    fun recalculateChunk(chunk: Chunk, chunkLight: ChunkLight) {
        chunk.sections.reversed().forEachIndexed { i, section ->
            recalculateSection(section, i, chunkLight, chunk)

            if (hasNonZeroData(chunk.chunkLight.skyLight[i])) {
                chunkLight.skyMask.set(i)
            } else {
                chunkLight.emptySkyMask.set(i)
            }

            if (hasNonZeroData(chunk.chunkLight.blockLight[i])) {
                chunkLight.blockMask.set(i)
            } else {
                chunkLight.emptyBlockMask.set(i)
            }
        }
    }

    fun recalculateSection(section: ChunkSection, sectionIndex: Int, chunkLight: ChunkLight, chunk: Chunk) {
        recalcArray = ByteArray(ARRAY_SIZE)

        for (x in 0..15) {
            for (z in 0..15) {
                var foundSolid = false
                for (y in 15 downTo 0) {
                    val isSolid = section.blockPalette[x, y, z] != 0
                    var light = 15

                    if (isSolid) {
                        foundSolid = true
                    }

                    if (foundSolid) {
                        light = 0
                    }

                    if(x == 1 && z == 1) light = 0

                    set(getCoordIndex(x, y, z), light)
                }
            }
        }

//        chunkLight.skyLight[sectionIndex] = recalcArray
        chunkLight.blockLight[sectionIndex] = recalcArray
    }

    // operation type: updating
    operator fun set(x: Int, y: Int, z: Int, value: Int) {
        this[x and 15 or (z and 15 shl 4) or (y and 15 shl 8)] = value
    }

    // https://github.com/PaperMC/Starlight/blob/6503621c6fe1b798328a69f1bca784c6f3ffcee3/src/main/java/ca/spottedleaf/starlight/common/light/SWMRNibbleArray.java#L410
    // operation type: updating
    operator fun set(index: Int, value: Int) {
        val shift = index and 1 shl 2
        val i = index ushr 1
        recalcArray[i] = (recalcArray[i].toInt() and (0xF0 ushr shift) or (value shl shift)).toByte()
    }

    fun canLightPassThrough(id: Int): Boolean {
        return id == 0
//        val block = Block.getBlockByStateId(id)
//        return !block.registryBlock.isSolid
    }

    fun getCoordIndex(x: Int, y: Int, z: Int): Int {
        return y shl dimension / 2 or (z shl dimension / 4) or x
    }

    var printed = 0
    fun shrink(array: ByteArray): ByteArray {
        val shrunk = ByteArray(ARRAY_SIZE)
        var i = 0
        while (i < array.size) {
            val j = i + 1
            val iB = array[i]
            val jB = array[j]
            val merged = (array[i].toInt() shl 4 or array[j].toInt()).toByte()
            if (printed < 10) {
                println(Integer.toBinaryString(iB.toInt()))
                println(Integer.toBinaryString(jB.toInt()))
                println(Integer.toBinaryString(merged.toInt()))
                println(i)
                println(j)
                println("---------------")
                printed++
            }
            shrunk[i / 2] = (array[i].toInt() shl 4 or array[j].toInt()).toByte()
            i += 2
        }
        return shrunk
    }

    private fun hasNonZeroData(array: ByteArray?): Boolean {
        if (array == null) return false
        for (i in array.indices) {
            if (array[i] != 0.toByte()) return true
        }
        return false
    }
}