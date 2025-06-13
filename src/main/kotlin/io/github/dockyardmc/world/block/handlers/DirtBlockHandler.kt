package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Tags
import io.github.dockyardmc.world.block.Block

class DirtBlockHandler : BlockHandler {
    override fun onUse(player: Player, hand: PlayerHand, heldItem: ItemStack, block: Block, face: Direction, location: Location, cursor: Vector3f): Boolean {
        if(!Tags.ITEM_SHOVELS.contains(heldItem.material.identifier)) return false
        if(!location.add(0, 1, 0).block.isAir()) return false

        location.setBlock(Blocks.DIRT_PATH)
        return true
    }
}
