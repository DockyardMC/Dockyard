package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block

class DirtPathBlockHandler : BlockHandler {
    override fun onUpdateByNeighbour(block: Block, world: World, location: Location, neighbour: Block, neighbourLocation: Location) {
        if(!location.add(0, 1, 0).block.isAir())
            location.setBlock(Blocks.DIRT)
    }
}