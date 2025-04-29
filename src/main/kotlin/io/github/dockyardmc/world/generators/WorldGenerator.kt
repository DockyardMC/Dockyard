package io.github.dockyardmc.world.generators

import io.github.dockyardmc.registry.registries.Biome
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block

interface WorldGenerator {
    fun getBlock(x: Int, y: Int, z: Int): Block

    fun getBiome(x: Int, y: Int, z: Int): Biome

    fun onWorldLoad(world: World) {}

    val generateBaseChunks: Boolean
        get() = true
}