package io.github.dockyardmc.blocks

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.getDirection
import io.github.dockyardmc.player.getOpposite
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.withBlockStates

class RotationPlacementRules(var matches: List<String>): BlockPlacementRule {
    override val matchesIdentifier = ""

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
        if(!matches.contains(block.namespace)) return block

        val dir = player.getDirection(true).getOpposite()
        return block.withBlockStates("facing" to dir.name.lowercase() )
    }
}