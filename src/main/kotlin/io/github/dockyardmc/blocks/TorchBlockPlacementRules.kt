package io.github.dockyardmc.blocks

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.getDirection
import io.github.dockyardmc.player.getOpposite
import io.github.dockyardmc.registry.Blocks

class TorchBlockPlacementRules: BlockPlacementRule {
    override val matchesIdentifier = "torch"

    override fun getPlacement(
        player: Player,
        heldItem: ItemStack,
        block: Block,
        face: Direction,
        location: Location,
        clickedBlock: Location,
        cursorX: Float,
        cursorY: Float,
        cursorZ: Float,
    ): Block? {

        if(face == Direction.DOWN) return null
        if(face == Direction.UP) return block

        val final = when (block.registryBlock) {
            Blocks.TORCH -> Blocks.WALL_TORCH
            Blocks.SOUL_TORCH -> Blocks.SOUL_WALL_TORCH
            Blocks.REDSTONE_TORCH -> Blocks.REDSTONE_WALL_TORCH
            else -> block.registryBlock
        }

        return final.withBlockStates("facing" to player.getDirection(true).getOpposite().name.lowercase())
    }

}