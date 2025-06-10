package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block

class RedstoneDustHandler : BlockHandler {

    companion object {
        val CAN_CONNECT: List<RegistryBlock> = listOf(
            Blocks.COMMAND_BLOCK,
            Blocks.CHAIN_COMMAND_BLOCK,
            Blocks.REPEATING_COMMAND_BLOCK,
            Blocks.REPEATER,
            Blocks.COMPARATOR,
            Blocks.REDSTONE_WIRE,
            Blocks.REDSTONE_LAMP
        )
    }

    override fun onUpdateByNeighbour(block: Block, world: World, location: Location, neighbour: Block, neighbourLocation: Location) {
        location.setBlock(getBlock(location, block))
    }

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        return getBlock(location, block)
    }

    private fun getBlock(location: Location, block: Block): Block {
        val north = location.relative(Direction.NORTH)
        val east = location.relative(Direction.EAST)
        val south = location.relative(Direction.SOUTH)
        val west = location.relative(Direction.WEST)

        val newBlock = block.withBlockStates(
            mapOf(
                "north" to canConnect(north, Direction.NORTH),
                "east" to canConnect(east, Direction.EAST),
                "south" to canConnect(south, Direction.SOUTH),
                "west" to canConnect(west, Direction.WEST),
            )
        )

        return newBlock
    }


    private fun isPowered(block: Block): Boolean {
        return block.blockStates["power"] != null
    }

    private fun canConnect(location: Location, direction: Direction): String {
        val isFaceFull = !location.block.isAir()
        val canConnect = CAN_CONNECT.contains(location.block.registryBlock)
        return if(isFaceFull && canConnect) "side" else "none"
    }

}