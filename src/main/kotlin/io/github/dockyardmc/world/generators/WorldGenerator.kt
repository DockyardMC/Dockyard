package io.github.dockyardmc.world.generators

import io.github.dockyardmc.registry.Biome
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.world.World

interface WorldGenerator {
    val world: World

    fun getBlock(x: Int, y: Int, z: Int): Block

    fun getBiome(x: Int, y: Int, z: Int): Biome

}