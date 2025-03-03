package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.toVector3f
import io.github.dockyardmc.utils.vectors.Vector3f
import io.github.dockyardmc.world.block.Block

class SlabHandler : BlockHandler {

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        var existingBlockLocation = clickedBlock.add(face.toVector3f())
        var existingBlock = existingBlockLocation.block
        val states = mutableMapOf<String, String>()

        states["type"] = if (cursor.y >= 0.5f && face != Direction.UP) "top" else "bottom"
        if (face == Direction.DOWN) states["type"] = "top"

        if (clickedBlock.block.identifier == block.identifier
            && clickedBlock.block.blockStates["type"] != "double"
            && (face == Direction.DOWN || face == Direction.UP)
        ) {

            if ((clickedBlock.block.blockStates["type"] == "top" && face == Direction.DOWN) ||
                (clickedBlock.block.blockStates["type"] == "bottom" && face == Direction.UP)
            ) {

                existingBlock = clickedBlock.block
                existingBlockLocation = clickedBlock
            }
        }

        if (existingBlock.blockStates["type"] == "bottom") {
            clickedBlock.world.setBlockState(existingBlockLocation, "type" to "double")
            return null
        }

        if (existingBlock.blockStates["type"] == "top") {
            clickedBlock.world.setBlockState(existingBlockLocation, "type" to "double")
            return null
        }

        return block.withBlockStates(states)
    }
}