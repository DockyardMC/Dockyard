package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block

class DoublePlantBlockHandler : BlockHandler {
    companion object {
        val doublePlants: List<RegistryBlock> = listOf(
            Blocks.SUNFLOWER,
            Blocks.LILAC,
            Blocks.ROSE_BUSH,
            Blocks.PEONY,
            Blocks.TALL_GRASS,
            Blocks.LARGE_FERN
        )
    }

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        val blockAbove = location.add(0, 1, 0)
        location.world.setBlock(blockAbove, block.withBlockStates("half" to "upper"))
        return block.withBlockStates("half" to "lower")
    }

    override fun onDestroy(block: Block, world: World, location: Location) {
        val blockAbove = location.add(0, 1, 0)
        val blockBelow = location.add(0, -1, 0)
        if(blockAbove.block.registryBlock == block.registryBlock && blockAbove.block.blockStates["half"] == "upper") {
            blockAbove.world.destroyNaturally(blockAbove)
        }
        if(blockBelow.block.registryBlock == block.registryBlock && blockBelow.block.blockStates["half"] == "lower") {
            blockBelow.world.destroyNaturally(blockBelow)
        }
    }
}