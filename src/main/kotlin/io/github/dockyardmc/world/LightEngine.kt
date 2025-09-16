package io.github.dockyardmc.world

import io.github.dockyardmc.extentions.toByteBuf
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.world.block.Block
import io.github.dockyardmc.world.chunk.Chunk
import io.github.dockyardmc.world.chunk.ChunkSection
import io.netty.buffer.ByteBuf
import java.util.*

class LightEngine(
    val chunk: Chunk
) {
    companion object {
        const val ARRAY_SIZE = 2048
    }

    lateinit var recalcArray: ByteArray

    val skyLight: Array<ByteArray> = Array(chunk.maxSection - chunk.minSection) { ByteArray(0) }
    val blockLight: Array<ByteArray> = Array(chunk.maxSection - chunk.minSection) { ByteArray(0) }


    fun createLightData(): LightData {
        val skyMask = BitSet()
        val blockMask = BitSet()
        val emptySkyMask = BitSet()
        val emptyBlockMask = BitSet()
        val skyLight = mutableListOf<ByteBuf>()
        val blockLight = mutableListOf<ByteBuf>()

        // first section is below the world. how awesome. we love minecraft
        emptySkyMask.set(0)
        emptyBlockMask.set(0)

        // last section is one section above the world. why.
        emptySkyMask.set(this.skyLight.size + 1)
        emptyBlockMask.set(this.skyLight.size + 1)

        this.skyLight.indices.forEach { i ->
            if (this.hasNonZeroData(this.skyLight[i])) {
                skyMask.set(i + 1)
                skyLight.add(this.skyLight[i].toByteBuf())
            } else {
                emptySkyMask.set(i + 1)
            }

            if (this.hasNonZeroData(this.blockLight[i])) {
                blockMask.set(i + 1)
                blockLight.add(this.blockLight[i].toByteBuf())
            } else {
                emptyBlockMask.set(i + 1)
            }
        }

        return LightData(skyMask, blockMask, emptySkyMask, emptyBlockMask, skyLight, blockLight)
    }

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
                    var light = 0
//                    var light = 15

                    foundSolid = foundSolid || section.getBlock(x, y, z) != 0

                    if (foundSolid) {
                        light = 0
                    }

                    set(x, y, z, light)
                }
            }
        }
        skyLight[sectionIndex] = recalcArray
        recalculateBlockLight(section, sectionIndex)
    }

    fun recalculateBlockLight(section: ChunkSection, sectionIndex: Int) {
        recalcArray = ByteArray(ARRAY_SIZE)
        val lightQueue: Queue<Vector3> = ArrayDeque()

        for (x in 0..15) {
            for (z in 0..15) {
                for (y in 15 downTo 0) {
                    var light = 0

                    val blockId = section.getBlock(x, y, z)
                    val block = Block.getBlockByStateId(blockId).registryBlock

                    if (block.lightEmission > 0) {
                        light = block.lightEmission
                        lightQueue.add(Vector3(x, y, z))
                    }

                    set(x, y, z, light)
                }
            }
        }

        lightPropagation(lightQueue, section, sectionIndex)
        blockLight[sectionIndex] = recalcArray
    }

    fun lightPropagation(queue: Queue<Vector3>, section: ChunkSection, sectionIndex: Int) {
        val directions = arrayOf(
            Vector3(1, 0, 0), Vector3(-1, 0, 0),
            Vector3(0, 1, 0), Vector3(0, -1, 0),
            Vector3(0, 0, 1), Vector3(0, 0, -1)
        )

        while (queue.isNotEmpty()) {
            val (x, y, z) = queue.poll()
            val currentLightLevel = get(x, y, z)

            if (currentLightLevel <= 1) {
                continue
            }

            val newLightLevel = currentLightLevel - 1

            for ((dX, dY, dZ) in directions) {
                val neighborX = x + dX
                val neighborY = y + dY
                val neighborZ = z + dZ
                if (neighborX in 0..15 && neighborY in 0..15 && neighborZ in 0..15) {
                    val neighborBlockId = section.getBlock(neighborX, neighborY, neighborZ)
                    val neighborBlock = Block.getBlockByStateId(neighborBlockId).registryBlock

                    if (!neighborBlock.canOcclude) {
                        val neighborCurrentLight = get(neighborX, neighborY, neighborZ)

                        if (newLightLevel > neighborCurrentLight) {
                            set(neighborX, neighborY, neighborZ, newLightLevel)
                            queue.add(Vector3(neighborX, neighborY, neighborZ))
                        }
                    }
                }
            }
        }
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

    operator fun get(x: Int, y: Int, z: Int): Int {
        return this[x or (z shl 4) or (y shl 8)]
    }

    operator fun get(index: Int): Int {
        val shift = index and 1 shl 2
        val i = index ushr 1

        return (recalcArray[i].toInt() ushr shift) and 0xF
    }

    fun hasNonZeroData(array: ByteArray?): Boolean {
        if (array == null) return false
        return array.isNotEmpty() && array.any { it != 0.toByte() }
    }
}
