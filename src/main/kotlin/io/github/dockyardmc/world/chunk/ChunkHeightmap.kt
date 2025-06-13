package io.github.dockyardmc.world.chunk

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.maths.ceilLog2
import io.github.dockyardmc.utils.bitstorage.SimpleBitStorage
import io.github.dockyardmc.world.block.Block
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import java.util.function.Predicate

class ChunkHeightmap(val chunk: Chunk, val type: Type) {

    private val bitStorage = SimpleBitStorage(ceilLog2(chunk.world.dimensionType.height + 1), 256)

    companion object {
        private val NOT_AIR: Predicate<Block> = Predicate { block -> !block.isAir() }
        private val BLOCKS_MOTION: Predicate<Block> = Predicate { block -> block.registryBlock.isSolid }

        fun generate(chunk: Chunk, toGenerate: Set<Type>) {
            val size = toGenerate.size
            val heightmaps = ObjectArrayList<ChunkHeightmap>(size)
            val iterator = heightmaps.iterator()
            val highest = chunk.highestSectionY() + 16
            for (x in 0 until 16) {
                for (z in 0 until 16) {
                    toGenerate.forEach { type -> heightmaps.add(chunk.getOrCreateHeightmap(type)) }
                    for (y in highest - 1 downTo chunk.world.dimensionType.minY) {
                        val block = chunk.getBlock(x, y, z)
                        if (!block.isAir()) {
                            while (iterator.hasNext()) {
                                val heightmap = iterator.next()
                                if (!heightmap.type.predicate.test(block)) continue
                                heightmap.set(x, z, y + 1)
                                iterator.remove()
                            }
                            if (heightmaps.isEmpty) break
                            iterator.back(size)
                        }
                    }
                }
            }
        }
    }

    fun update(x: Int, y: Int, z: Int, block: Block): Boolean {
        val firstAvailable = firstAvailable(x, z)
        if (y <= firstAvailable - 2) return false

        if (type.predicate.test(block)) {
            if (y >= firstAvailable) {
                set(x, z, y + 1)
                return true
            }
        } else if (firstAvailable - 1 == y) {
            for (i in y downTo chunk.world.dimensionType.minY) {
                if (type.predicate.test(chunk.getBlock(x, i, z))) {
                    set(x, z, i + 1)
                    return true
                }
            }

            set(x, z, chunk.world.dimensionType.minY)
            return true
        }

        return false
    }

    fun setData(chunk: Chunk, type: Type, rawData: LongArray) {
        val current = bitStorage.data

        if (current.size != rawData.size) {
            log("Ignoring heightmap data for chunk ${chunk.chunkPos} as the size is not what was expected (${current.size} != ${rawData.size})", LogType.WARNING)
            generate(chunk, setOf(type))
            return
        }

        System.arraycopy(rawData, 0, current, 0, rawData.size)
    }

    @Suppress("AddOperatorModifier")
    fun set(x: Int, z: Int, y: Int) {
        bitStorage[indexOf(x, z)] = y - chunk.world.dimensionType.minY
    }

    private fun indexOf(x: Int, z: Int): Int = x + z * 16

    private fun firstAvailable(x: Int, z: Int): Int = firstAvailable(indexOf(x, z))
    private fun firstAvailable(index: Int): Int = bitStorage[index] + chunk.world.dimensionType.minY

    enum class Type(val usage: Usage, val predicate: Predicate<Block>) {
        WORLD_SURFACE_WG(Usage.WORLD_GENERATION, NOT_AIR),
        WORLD_SURFACE(Usage.CLIENT, NOT_AIR),
        OCEAN_FLOOR_WG(Usage.WORLD_GENERATION, BLOCKS_MOTION),
        OCEAN_FLOOR(Usage.LIVE_WORLD, BLOCKS_MOTION),
        MOTION_BLOCKING(Usage.CLIENT, { block -> block.registryBlock.isSolid || block.registryBlock.isLiquid }),
        MOTION_BLOCKING_NO_LEAVES(Usage.LIVE_WORLD, { block -> block.registryBlock.isSolid && !block.identifier.endsWith("_leaves") });

        fun sendToClient(): Boolean = usage == Usage.CLIENT
    }

    fun getRawData(): LongArray = bitStorage.data

    enum class Usage {
        WORLD_GENERATION,
        LIVE_WORLD,
        CLIENT
    }
}