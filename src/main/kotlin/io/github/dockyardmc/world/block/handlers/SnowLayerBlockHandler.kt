package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.world.block.Block

class SnowLayerBlockHandler : BlockHandler {

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        val blockBelow = location.subtract(0, 1, 0)

        if (blockBelow.block.registryBlock == block.registryBlock) {
            val layers = blockBelow.block.blockStates["layers"]?.toInt() ?: 1
            val newLayers = (layers + 1).coerceAtMost(8)

            val newBlock = if (newLayers == 8) {
                Blocks.SNOW_BLOCK.toBlock()
            } else {
                block.withBlockStates("layers" to newLayers.toString())
            }
            blockBelow.setBlock(newBlock)
            return null
        }

        return block.withBlockStates("layers" to "1")
    }
}