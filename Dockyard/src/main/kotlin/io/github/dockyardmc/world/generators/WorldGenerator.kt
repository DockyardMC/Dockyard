package io.github.dockyardmc.world.generators

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.registry.registries.Biome

interface WorldGenerator {
    fun getBlock(x: Int, y: Int, z: Int): Block

    fun getBiome(x: Int, y: Int, z: Int): Biome
}