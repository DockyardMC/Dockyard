package io.github.dockyardmc.world.generators

import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.Biome

open class VoidWorldGenerator(val defaultBiome: Biome = Biomes.THE_VOID): WorldGenerator {
    override fun getBlock(x: Int, y: Int, z: Int): io.github.dockyardmc.world.block.Block = Blocks.AIR.toBlock()

    override fun getBiome(x: Int, y: Int, z: Int): Biome = defaultBiome

    override val generateBaseChunks: Boolean
        get() = true
}