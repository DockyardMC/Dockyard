package io.github.dockyardmc.world.generators

import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.world.World

class FlatWorldGenerator(override val world: World) : WorldGenerator {

    override fun getBlock(x: Int, y: Int, z: Int): Block {
        return if(y <= 200) Blocks.GRASS_BLOCK else Blocks.AIR
    }
}