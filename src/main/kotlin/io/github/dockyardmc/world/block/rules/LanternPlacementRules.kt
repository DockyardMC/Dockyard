package io.github.dockyardmc.world.block.rules

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player

class LanternPlacementRules : BlockPlacementRule {
    override val matchesIdentifier = "lantern"

    override fun getPlacement(
        player: Player,
        heldItem: ItemStack,
        block: io.github.dockyardmc.world.block.Block,
        face: Direction,
        location: Location,
        clickedBlock: Location,
        cursorX: Float,
        cursorY: Float,
        cursorZ: Float,
    ): io.github.dockyardmc.world.block.Block? {

        val hanging = if (face == Direction.DOWN) "true" else "false"
        val final = block.withBlockStates("hanging" to hanging)
        if (face != Direction.DOWN && face != Direction.UP) {
            val blockBelow = location.world.getBlock(location.subtract(0, 1, 0))
            return if (blockBelow.registryBlock.isAir && blockBelow.registryBlock.isSolid) null else final
        }
        return final
    }
}