package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.world.block.Block

class CandleBlockHandler : BlockHandler {
    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        val theBlock: Location = if (clickedBlock.block.registryBlock == block.registryBlock && clickedBlock.block.blockStates["candles"] != "4") {
            clickedBlock
        } else if (location.block.registryBlock == block.registryBlock) {
            location
        } else {
            return block
        }

        var candles = theBlock.block.blockStates["candles"]?.toIntOrNull() ?: 1
        candles = (candles + 1).coerceAtMost(4)

        theBlock.setBlock(theBlock.block.withBlockStates("candles" to candles.toString()))
        return null
    }

    override fun onUse(player: Player, hand: PlayerHand, heldItem: ItemStack, block: Block, face: Direction, location: Location, cursor: Vector3f): Boolean {
        if(heldItem.material == Items.FLINT_AND_STEEL && block.blockStates["lit"] != "true") {
            location.setBlock(block.withBlockStates("lit" to "true"))
            return true
        }
        return false
    }
}
