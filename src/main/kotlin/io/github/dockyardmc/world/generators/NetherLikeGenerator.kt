package io.github.dockyardmc.world.generators

import FastNoiseLite
import io.github.dockyardmc.registry.Biome
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.Vector3
import java.util.*
import kotlin.random.Random

class NetherLikeGenerator: WorldGenerator {

    val baseNoiseGenerator = FastNoiseLite()
    val surfaceNoiseGenerator = FastNoiseLite()

    val seaLevel = 87
    val random = Random(UUID.randomUUID().mostSignificantBits)

    val decorator: MutableMap<Vector3, Block> = mutableMapOf()

    init {
        baseNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S)
        surfaceNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.Cellular)
        baseNoiseGenerator.SetFrequency(0.030f)
        surfaceNoiseGenerator.SetFrequency(0.0072f)
    }

    override fun getBlock(x: Int, y: Int, z: Int): Block {
        val amplitude = 15f
        val baseNoise = baseNoiseGenerator.GetNoise(x.toFloat(), z.toFloat())
        val surfaceNoise = surfaceNoiseGenerator.GetNoise(x.toFloat(), z.toFloat())

        val surfaceY = (100 + (baseNoise + surfaceNoise) * amplitude).toInt()

        var block = if(y < surfaceY) Blocks.SMOOTH_BASALT else Blocks.AIR
        if(block == Blocks.AIR) {
            if(y < seaLevel) block = Blocks.LAVA
        }

        if(block == Blocks.SMOOTH_BASALT) {
            if(y <= seaLevel + 1) block = Blocks.MAGMA_BLOCK
        }

        if(block == Blocks.SMOOTH_BASALT) {
            val random = mutableListOf(true, true, false).random()
            if(y >= 93 && random) {
                block = Blocks.SOUL_SAND
            }
            val randomSprout = MathUtils.randomInt(0, 25)
            if( y >= 96) {
                block = Blocks.SOUL_SOIL
                if(randomSprout == 5) {
                    decorator[Vector3(x, y + 1, z)] = Blocks.CRIMSON_ROOTS
                }
                if(randomSprout == 0) {
                    decorator[Vector3(x, y + 1, z)] = Blocks.CRIMSON_FUNGUS
                }
            }
        }

        if(block == Blocks.AIR) {
            if(decorator[Vector3(x, y, z)] != null) {
                val decoBlock = decorator[Vector3(x, y, z)]!!
                block = decoBlock
                decorator.remove(Vector3(x, y, z))
            }
        }

//        if(y == surfaceY -1 && block == Blocks.STONE) block = Blocks.GRASS_BLOCK

        return block
    }

    override fun getBiome(x: Int, y: Int, z: Int): Biome = Biomes.BASALT_DELTAS
}