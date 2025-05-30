package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.world.block.Block
import io.github.dockyardmc.world.block.handlers.BlockHandlerUtil.getAxis

class AxisFacingBlockHandler : BlockHandler {

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        return block.withBlockStates("axis" to getAxis(face).name.lowercase())
    }

}