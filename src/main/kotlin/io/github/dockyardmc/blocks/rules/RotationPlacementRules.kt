package io.github.dockyardmc.blocks.rules

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.getDirection
import io.github.dockyardmc.player.getOpposite

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
        if(!matches.contains(block.identifier.replace("minecraft:", ""))) return block

        var dir = face
        if(face == Direction.UP || face == Direction.DOWN) dir = player.getDirection(true).getOpposite()
        return block.withBlockStates("facing" to dir.name.lowercase() )
    }
}