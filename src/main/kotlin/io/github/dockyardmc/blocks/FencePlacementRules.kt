package io.github.dockyardmc.blocks

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.withBlockStates

class FencePlacementRules: BlockPlacementRule {
    override val matchesIdentifier = "fence"

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

        if(north.namespace.contains(matchesIdentifier)) {
            states["north"] = "true"
            world.setBlockState(northLoc, "south" to "true")
        }
        if(south.namespace.contains(matchesIdentifier)) {
            states["south"] = "true"
            world.setBlockState(southLoc, "north" to "true")
        }
        if(east.namespace.contains(matchesIdentifier)) {
            states["east"] = "true"
            world.setBlockState(eastLoc, "west" to "true")
        }
        if(west.namespace.contains(matchesIdentifier)) {
            states["west"] = "true"
            world.setBlockState(westLoc, "east" to "true")
        }

        return block.withBlockStates(states)
    }
}