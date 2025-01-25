package io.github.dockyardmc.blocks.rules

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player

class LogBlockPlacementRules: BlockPlacementRule {
    override val matchesIdentifier = "log"

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

        val axis: String = when(face) {
            Direction.DOWN,
            Direction.UP -> "y"
            Direction.SOUTH,
            Direction.NORTH -> "z"
            Direction.EAST,
            Direction.WEST -> "x"
        }

        return block.withBlockStates("axis" to axis)
    }
}