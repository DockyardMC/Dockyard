package io.github.dockyardmc.world.generators

import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.Biome

class FlatWorldGenerator(val defaultBiome: Biome = Biomes.PLAINS) : WorldGenerator {

    override fun getBlock(x: Int, y: Int, z: Int): io.github.dockyardmc.world.block.Block {
        return when {
            y == 200 -> Blocks.GRASS_BLOCK.toBlock()
            y < 195 -> Blocks.STONE.toBlock()
            y < 200 -> Blocks.DIRT.toBlock()
            else -> Blocks.AIR.toBlock()
        }
    }

    override fun getBiome(x: Int, y: Int, z: Int): Biome = defaultBiome
}