package io.github.dockyardmc.world.chunk

import io.github.dockyardmc.world.block.Block
import java.util.function.Predicate

class ChunkHeightmap(val chunk: Chunk, val type: Type) {

    companion object {
        private val NOT_AIR: Predicate<Block> = Predicate { block -> !block.isAir() }
        private val BLOCKS_MOTION: Predicate<Block> = Predicate { block -> block.registryBlock.isSolid }
    }

    fun update(x: Int, y: Int, z: Int, block: Block) {

    }

    enum class Type(val usage: Usage, val predicate: Predicate<Block>) {
        WORLD_SURFACE_WG(Usage.WORLD_GENERATION, NOT_AIR),
        WORLD_SURFACE(Usage.CLIENT, NOT_AIR),
        OCEAN_FLOOR_WG(Usage.WORLD_GENERATION, BLOCKS_MOTION),
        OCEAN_FLOOR(Usage.LIVE_WORLD, BLOCKS_MOTION),
        MOTION_BLOCKING(Usage.CLIENT, { block -> block.registryBlock.isSolid || block.registryBlock.isLiquid }),
        MOTION_BLOCKING_NO_LEAVES(Usage.LIVE_WORLD, { block -> block.registryBlock.isSolid && !block.identifier.endsWith("_leaves") }),
    }

    enum class Usage {
        WORLD_GENERATION,
        LIVE_WORLD,
        CLIENT
    }
}