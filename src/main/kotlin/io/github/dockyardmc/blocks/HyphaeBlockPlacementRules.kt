package io.github.dockyardmc.blocks

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.getDirection
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.withBlockStates

class HyphaeBlockPlacementRules: BlockPlacementRule {
    override val matchesIdentifier = "hyphae"

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
    ): Block {

        var inputDirection = face
        if(face == Direction.UP) {
            inputDirection = player.getDirection(true)
        }

        val axis: String = when(inputDirection) {
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