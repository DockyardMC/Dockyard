package io.github.dockyardmc.world.block.rules

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.getDirection

class StairBlockPlacementRules: BlockPlacementRule {
    override val matchesIdentifier = "stair"

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
        val originalBlock = clickedBlock.world.getBlock(clickedBlock)
        val states = mutableMapOf<String, String>()

        val direction = player.getDirection(true)
        states["facing"] = direction.name.lowercase()

        states["half"] = if(cursorY >= 0.5f) "top" else "bottom"

        if(face == Direction.UP) states["half"] = "bottom"
        if(face == Direction.DOWN) states["half"] = "top"

        return block.withBlockStates(states)
    }
}