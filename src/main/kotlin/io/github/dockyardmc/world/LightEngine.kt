package io.github.dockyardmc.world

import io.github.dockyardmc.world.chunk.Chunk
import io.github.dockyardmc.world.chunk.ChunkSection

class LightEngine(
    val chunk: Chunk
) {
    companion object {
        const val ARRAY_SIZE = 2048
    }

    lateinit var recalcArray: ByteArray

    val skyLight: Array<ByteArray> = Array(chunk.maxSection - chunk.minSection) { ByteArray(0) }
    val blockLight: Array<ByteArray> = Array(chunk.maxSection - chunk.minSection) { ByteArray(0) }

    fun recalculateChunk() {
        chunk.sections.forEachIndexed { i, section ->
            recalculateSection(section, i)
        }
    }

    fun recalculateSection(section: ChunkSection, sectionIndex: Int) {
        recalcArray = ByteArray(ARRAY_SIZE)

        for (x in 0..15) {
            for (z in 0..15) {
                var foundSolid = false
                for (y in 15 downTo 0) {
                    var light = 15

                    foundSolid = foundSolid || section.getBlock(x, y, z) != 0

                    if (foundSolid) {
                        light = 0
                    }

                    set(x, y, z, light)
                    //if(light == 0) {
                    //    log("Writing light 0 for $x, $y, $z (section index $sectionIndex)")
                    //    log("${recalcArray[getCoordIndex(x, y, z)  ushr 1]}")
                    //}
                }
            }
        }

        skyLight[sectionIndex] = recalcArray
    }

    operator fun set(x: Int, y: Int, z: Int, value: Int) {
        this[x or (z shl 4) or (y shl 8)] = value
    }

    // https://github.com/PaperMC/Starlight/blob/6503621c6fe1b798328a69f1bca784c6f3ffcee3/src/main/java/ca/spottedleaf/starlight/common/light/SWMRNibbleArray.java#L410
    // operation type: updating
    operator fun set(index: Int, value: Int) {
        val shift = index and 1 shl 2
        val i = index ushr 1
        recalcArray[i] = (recalcArray[i].toInt() and (0xF0 ushr shift) or (value shl shift)).toByte()
    }

    fun hasNonZeroData(array: ByteArray?): Boolean {
        if (array == null) return false
        return array.isNotEmpty() && array.any { it != 0.toByte() }
    }
}
