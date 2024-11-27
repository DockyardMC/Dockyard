package io.github.dockyardmc.blocks

import io.github.dockyardmc.blocks.rules.BlockPlacementRule
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player

class ShulkerboxPlacementRules: BlockPlacementRule {
    override val matchesIdentifier = "shulker_box"

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
        return block.withBlockStates("facing" to face.name.lowercase())
    }
}