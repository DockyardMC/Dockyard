package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.getDirection
import io.github.dockyardmc.player.getOpposite
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.world.block.Block

class FacingBlockHandler(val upDown: Boolean) : BlockHandler {

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        val direction = if (upDown || !(face == Direction.UP || face == Direction.DOWN)) {
            face
        } else {
            player.getDirection(true).getOpposite()
        }
        return block.withBlockStates("facing" to direction.name.lowercase())
    }

}