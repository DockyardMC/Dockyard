package io.github.dockyardmc.blocks

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.getDirection
import io.github.dockyardmc.player.getOpposite
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.withBlockStates

class TrapdoorBlockPlacementRule: BlockPlacementRule {
    override val matchesIdentifier = "trapdoor"

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

        states["half"] = if(cursorY >= 0.5f && face != Direction.UP) "top" else "bottom"
        if(face == Direction.DOWN) states["half"] = "top"

        var dir = face
        if(face == Direction.UP || face == Direction.DOWN) dir = player.getDirection(true).getOpposite()
        states["facing"] = dir.name.lowercase()

        return block.withBlockStates(states)
    }
}