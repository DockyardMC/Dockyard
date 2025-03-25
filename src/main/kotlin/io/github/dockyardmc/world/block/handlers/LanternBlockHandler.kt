package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.world.block.Block

class LanternBlockHandler: BlockHandler {

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        val hanging = if (face == Direction.DOWN) "true" else "false"
        val final = block.withBlockStates("hanging" to hanging)
        if (face != Direction.DOWN && face != Direction.UP) {
            val blockBelow = location.world.getBlock(location.subtract(0, 1, 0))
            return if (blockBelow.registryBlock.isAir && blockBelow.registryBlock.isSolid) null else final
        }
        return final
    }
}