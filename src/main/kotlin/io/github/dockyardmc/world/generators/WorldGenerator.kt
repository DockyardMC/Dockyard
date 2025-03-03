package io.github.dockyardmc.world.generators

import io.github.dockyardmc.registry.registries.Biome

interface WorldGenerator {
    fun getBlock(x: Int, y: Int, z: Int): io.github.dockyardmc.world.block.Block

    fun getBiome(x: Int, y: Int, z: Int): Biome
}