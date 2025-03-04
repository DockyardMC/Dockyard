package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.getDirection
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.vectors.Vector3f
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block

class DoorBlockHandler: BlockHandler {

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        val states = mutableMapOf<String, String>()

        val direction = player.getDirection(true)
        states["facing"] = direction.name.lowercase()

        val blockAbove = location.add(0, 1, 0)
        location.world.setBlock(blockAbove, block.withBlockStates("half" to "upper").withBlockStates(states))
        return block.withBlockStates(states)
    }

    override fun onDestroy(block: Block, world: World, location: Location) {
        val blockAbove = location.add(0, 1, 0)
        val blockBelow = location.add(0, -1, 0)
        if(blockAbove.block.registryBlock == block.registryBlock && blockAbove.block.blockStates["half"] == "upper") {
            blockAbove.world.destroyNaturally(blockAbove)
        }
        if(blockBelow.block.registryBlock == block.registryBlock && blockBelow.block.blockStates["half"] == "lower") {
            blockBelow.world.destroyNaturally(blockBelow)
        }
    }

    override fun onUse(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Boolean {
        if(block.registryBlock == Blocks.IRON_DOOR) return false
        val blockAbove = location.add(0, 1, 0)
        val blockBelow = location.add(0, -1, 0)

        val newOpenState = (!block.blockStates["open"].toBoolean()).toString()
        location.world.setBlockState(location, "open" to newOpenState)

        if(blockAbove.block.registryBlock == block.registryBlock && blockAbove.block.blockStates["half"] == "upper") {
            blockAbove.world.setBlockState(blockAbove, "open" to newOpenState)
        }

        if(blockBelow.block.registryBlock == block.registryBlock && blockBelow.block.blockStates["half"] == "lower") {
            blockBelow.world.setBlockState(blockBelow, "open" to newOpenState)
        }

        return true
    }
}