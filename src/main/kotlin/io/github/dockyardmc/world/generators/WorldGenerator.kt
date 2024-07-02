package io.github.dockyardmc.world.generators

import io.github.dockyardmc.registry.Biome
import io.github.dockyardmc.registry.Block

interface WorldGenerator {
    fun getBlock(x: Int, y: Int, z: Int): Block

    fun getBiome(x: Int, y: Int, z: Int): Biome
}