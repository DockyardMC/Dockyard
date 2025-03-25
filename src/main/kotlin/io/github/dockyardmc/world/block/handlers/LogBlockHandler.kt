package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.world.block.Block

class LogBlockHandler : BlockHandler {

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        val axis: String = when (face) {
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