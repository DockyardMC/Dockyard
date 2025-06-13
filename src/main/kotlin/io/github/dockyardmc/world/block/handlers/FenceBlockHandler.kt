package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block

class FenceBlockHandler : BlockHandler {

    companion object {
        val CANNOT_CONNECT: List<String> = listOf(
            "minecraft:barrier",
            "minecraft:carved_pumpkin",
            "minecraft:jack_o_lantern",
            "minecraft:melon",
            "minecraft:pumpkin",
        )
        val CANNOT_CONNECT_TAGS: List<String> = listOf(
            "minecraft:walls"
        )
    }

    override fun onUpdateByNeighbour(block: Block, world: World, location: Location, neighbour: Block, neighbourLocation: Location) {
        val north = location.relative(Direction.NORTH)
        val east = location.relative(Direction.EAST)
        val south = location.relative(Direction.SOUTH)
        val west = location.relative(Direction.WEST)

        val newBlock = block.withBlockStates(
            mapOf(
                "north" to canConnect(north, Direction.NORTH).toString(),
                "east" to canConnect(east, Direction.EAST).toString(),
                "south" to canConnect(south, Direction.SOUTH).toString(),
                "west" to canConnect(west, Direction.WEST).toString(),
            )
        )

        location.setBlock(newBlock)
    }

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {

        val north = location.relative(Direction.NORTH)
        val east = location.relative(Direction.EAST)
        val south = location.relative(Direction.SOUTH)
        val west = location.relative(Direction.WEST)

        return block.withBlockStates(
            mapOf(
                "north" to canConnect(north, Direction.NORTH).toString(),
                "east" to canConnect(east, Direction.EAST).toString(),
                "south" to canConnect(south, Direction.SOUTH).toString(),
                "west" to canConnect(west, Direction.WEST).toString(),
            )
        )
    }

    private fun canConnect(location: Location, direction: Direction): Boolean {
        val block = location.block
        val isNetherBrickFence = block.identifier.endsWith("_brick_fence")
        val canConnectToFence = canConnectToFence(block)
        val canFenceGateConnect = block.identifier.endsWith("_fence_gate") && BlockHandlerUtil.getAxis(BlockHandlerUtil.getDirection(block)) == BlockHandlerUtil.getAxis(BlockHandlerUtil.rotateYClockwise(direction))
        val isFaceFull = !location.block.isAir()

        return !cannotConnect(block) && isFaceFull || (canConnectToFence && !isNetherBrickFence) || canFenceGateConnect || isNetherBrickFence
    }

    private fun canConnectToFence(block: Block): Boolean {
        val isFence = block.registryBlock.tags.contains("minecraft:fences")
        val isWoodenFence = block.registryBlock.tags.contains("minecraft:wooden_fences")
        return isFence && isWoodenFence
    }

    private fun cannotConnect(block: Block): Boolean {
        return block.identifier.endsWith("leaves")
                ||
                block.identifier.endsWith("_shulker_box")
                ||
                CANNOT_CONNECT.contains(block.identifier)
                ||
                block.isAir()
                ||
                !block.registryBlock.isSolid
                ||
                block.tags.any { tag -> CANNOT_CONNECT_TAGS.contains(tag) }
    }
}