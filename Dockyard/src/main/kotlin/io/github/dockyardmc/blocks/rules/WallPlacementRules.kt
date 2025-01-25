package io.github.dockyardmc.blocks.rules

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player

class WallPlacementRules: BlockPlacementRule {
    override val matchesIdentifier = "wall"

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

        val world = location.world
        val northLoc = location.add(0, 0, -1)
        val southLoc = location.add(0, 0, 1)

        val eastLoc = location.add(1, 0, 0)
        val westLoc = location.add(-1, 0, 0)

        val north = world.getBlock(northLoc)
        val south = world.getBlock(southLoc)
        val east = world.getBlock(eastLoc)
        val west = world.getBlock(westLoc)

        if(north.identifier.contains(matchesIdentifier)) {
            states["north"] = "low"
            world.setBlockState(northLoc, "south" to "low")
        }
        if(south.identifier.contains(matchesIdentifier)) {
            states["south"] = "low"
            world.setBlockState(southLoc, "north" to "low")
        }
        if(east.identifier.contains(matchesIdentifier)) {
            states["east"] = "low"
            world.setBlockState(eastLoc, "west" to "low")
        }
        if(west.identifier.contains(matchesIdentifier)) {
            states["west"] = "low"
            world.setBlockState(westLoc, "east" to "low")
        }

        return block.withBlockStates(states)
    }
}