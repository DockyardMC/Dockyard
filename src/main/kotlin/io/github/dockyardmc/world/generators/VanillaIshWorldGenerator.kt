package io.github.dockyardmc.world.generators

import FastNoiseLite
import io.github.dockyardmc.registry.Biome
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import java.util.*

class VanillaIshWorldGenerator(var seed: Int = UUID.randomUUID().mostSignificantBits.toInt()) : WorldGenerator {

    val baseNoiseGenerator = FastNoiseLite()
    val surfaceNoiseGenerator = FastNoiseLite()
    val decoratorNoiseGenerator = FastNoiseLite()
    val riverNoiseGenerator = FastNoiseLite()

    val seaLevel = 84

    init {
        baseNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.Perlin)
        baseNoiseGenerator.SetSeed(seed)
        surfaceNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S)
        surfaceNoiseGenerator.SetSeed(seed)
        riverNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.Cellular)
        riverNoiseGenerator.SetSeed(seed)
        decoratorNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.Value)
        decoratorNoiseGenerator.SetSeed(seed)
        decoratorNoiseGenerator.SetFrequency(0.050f)
        riverNoiseGenerator.SetFrequency(0.007f)
    }

    override fun getBlock(x: Int, y: Int, z: Int): Block {

        val amplitude = 15f
        val baseNoise = baseNoiseGenerator.GetNoise(x.toFloat(), z.toFloat())
        val surfaceNoise = surfaceNoiseGenerator.GetNoise(x.toFloat(), z.toFloat())
        val decorationNoise = decoratorNoiseGenerator.GetNoise(x.toFloat(), z.toFloat())
        val riverNoise = riverNoiseGenerator.GetNoise(x.toFloat(), z.toFloat())

        val surfaceY = (100 + (baseNoise + surfaceNoise + decorationNoise - (riverNoise * -1)) * amplitude).toInt()

        var block = if(y < surfaceY) Blocks.STONE else Blocks.AIR
        if(block == Blocks.AIR) {
            if(y < seaLevel) block = Blocks.WATER
        }

        if(block == Blocks.STONE) {
            if(y <= seaLevel + 1) block = Blocks.SAND
        }

        if(y == surfaceY -1 && block == Blocks.STONE) block = Blocks.GRASS_BLOCK

        return block
    }

    override fun getBiome(x: Int, y: Int, z: Int): Biome = Biomes.PLAINS
}