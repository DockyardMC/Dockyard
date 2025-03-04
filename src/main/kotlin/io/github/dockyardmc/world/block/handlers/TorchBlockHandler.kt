package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.vectors.Vector3f
import io.github.dockyardmc.world.block.Block

class TorchBlockHandler: BlockHandler {

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        if(face == Direction.DOWN) return null
        if(face == Direction.UP) return block

        val final = when (block.registryBlock) {
            Blocks.TORCH -> Blocks.WALL_TORCH
            Blocks.SOUL_TORCH -> Blocks.SOUL_WALL_TORCH
            Blocks.REDSTONE_TORCH -> Blocks.REDSTONE_WALL_TORCH
            else -> block.registryBlock
        }

        return final.withBlockStates("facing" to face.name.lowercase())
    }
}