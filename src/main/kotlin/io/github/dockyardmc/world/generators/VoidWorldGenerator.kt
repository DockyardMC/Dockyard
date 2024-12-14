package io.github.dockyardmc.world.generators

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.Biome

class VoidWorldGenerator(val defaultBiome: Biome): WorldGenerator {
    override fun getBlock(x: Int, y: Int, z: Int): Block = Blocks.AIR.toBlock()

    override fun getBiome(x: Int, y: Int, z: Int): Biome = Biomes.THE_VOID
}