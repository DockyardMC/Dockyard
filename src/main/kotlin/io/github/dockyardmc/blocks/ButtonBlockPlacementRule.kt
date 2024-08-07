package io.github.dockyardmc.blocks

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.getDirection
import io.github.dockyardmc.player.getOpposite
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.withBlockStates

class ButtonBlockPlacementRule: BlockPlacementRule {
    override val matchesIdentifier = "button"

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
        val states = mutableMapOf<String, String>()

        if(face == Direction.UP) states["face"] = "floor"
        if(face == Direction.DOWN) states["face"] = "ceiling"

        var dir = face
        if(face == Direction.UP || face == Direction.DOWN) dir = player.getDirection(true).getOpposite()
        states["facing"] = dir.name.lowercase()

        return block.withBlockStates(states)
    }
}