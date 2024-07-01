package io.github.dockyardmc.world.generators

import io.github.dockyardmc.registry.Biome
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.world.World

class FlatWorldGenerator(override val world: World) : WorldGenerator {

    override fun getBlock(x: Int, y: Int, z: Int): Block {
        return when {
            y == 200 -> Blocks.GRASS_BLOCK
            y < 195 -> Blocks.STONE
            y < 200 -> Blocks.DIRT
            else -> Blocks.AIR
        }
    }

    override fun getBiome(x: Int, y: Int, z: Int): Biome = Biomes.SUNFLOWER_PLAINS
}