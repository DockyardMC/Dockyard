package io.github.dockyardmc.blocks.rules

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player

class SlabBlockPlacementRule: BlockPlacementRule {
    override val matchesIdentifier = "slab"

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
    ): Block? {
        val originalBlock = clickedBlock.world.getBlock(clickedBlock)
        val states = mutableMapOf<String, String>()

        states["type"] = if(cursorY >= 0.5f && face != Direction.UP) "top" else "bottom"
        if(face == Direction.DOWN) states["type"] = "top"

        if(originalBlock.blockStates["type"] == "bottom" && face == Direction.UP && originalBlock == block) {
            clickedBlock.world.setBlockState(clickedBlock, "type" to "double")
            return null
        }

        if(originalBlock.blockStates["type"] == "top" && face == Direction.DOWN && originalBlock == block) {
            clickedBlock.world.setBlockState(clickedBlock, "type" to "double")
            return null
        }

        return block.withBlockStates(states)
    }
}
