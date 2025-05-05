package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.*
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block

class StairBlockHandler : BlockHandler {

    override fun onUpdateByNeighbour(block: Block, world: World, location: Location, neighbour: Block, neighbourLocation: Location) {
        location.setBlock(location.block.withBlockStates("shape" to getShape(location.block, location)))
    }

    override fun onPlace(player: Player, heldItem: ItemStack, block: Block, face: Direction, location: Location, clickedBlock: Location, cursor: Vector3f): Block? {
        val states = mutableMapOf<String, String>()

        val direction = player.getDirection(true)
        states["facing"] = direction.name.lowercase()

        states["half"] = if (cursor.y >= 0.5f) "top" else "bottom"

        if (face == Direction.UP) states["half"] = "bottom"
        if (face == Direction.DOWN) states["half"] = "top"

        val shape = getShape(block.withBlockStates(states), location)
        states["shape"] = shape

        val finalBlock = block.withBlockStates(states)

        return finalBlock
    }

    // Based on cosrnic's Ice
    // https://github.com/cosrnic/Ice/blob/master/src/main/java/dev/cosrnic/ice/rules/StairsRule.java

    private fun getShape(block: Block, location: Location): String {
        val direction = getDirection(block)
        val offsetBlock = location.world.getBlock(location.add(direction.toNormalizedVector3f()))
        val offsetDirection = getDirection(offsetBlock)
        val oppositeOffsetBlock = location.world.getBlock(location.add(direction.getOpposite().toNormalizedVector3f()))

        val oppositeOffsetDirection = getDirection(oppositeOffsetBlock)

        if (isStairs(offsetBlock)
            &&
            getHalf(block) == getHalf(offsetBlock)
            &&
            getAxis(offsetDirection) != getAxis(direction)
            &&
            isDifferentOrientation(block, location, offsetDirection.getOpposite())
        ) {
            return if (offsetDirection == rotateYCounterclockwise(direction)) {
                "outer_left"
            } else {
                "outer_right"
            }
        }

        if (isStairs(oppositeOffsetBlock)
            &&
            getHalf(block) == getHalf(oppositeOffsetBlock)
            &&
            getAxis(oppositeOffsetDirection) != getAxis(direction)
            &&
            isDifferentOrientation(block, location, oppositeOffsetDirection)
        ) {
            return if (oppositeOffsetDirection == rotateYCounterclockwise(direction)) {
                "inner_left"
            } else {
                "inner_right"
            }
        }

        return "straight"
    }

    private fun getHalf(block: Block): String {
        return block.blockStates["half"] ?: "bottom"
    }

    private fun getDirection(block: Block): Direction {
//        log(block.blockStates.toString(), LogType.CRITICAL)
        val direction = block.blockStates["facing"] ?: "north"
        return Direction.valueOf(direction.uppercase())
    }

    fun getAxis(direction: Direction): Axis {
        val axis = when (direction) {
            Direction.DOWN -> Axis.Y
            Direction.UP -> Axis.Y

            Direction.NORTH -> Axis.Z
            Direction.SOUTH -> Axis.Z

            Direction.WEST -> Axis.X
            Direction.EAST -> Axis.X
        }
        return axis
    }

    fun isDifferentOrientation(block: Block, location: Location, direction: Direction): Boolean {
        val facing = getDirection(block)
        val half = block.blockStates["half"] ?: "bottom"
        val worldBlock = location.world.getBlock(location.add(direction.toNormalizedVector3f()))
        val worldBlockFacing = getDirection(worldBlock)
        val worldBlockHalf = worldBlock.blockStates["half"] ?: "bottom"

        val isDif = !isStairs(worldBlock) || worldBlockFacing != facing || worldBlockHalf != half
        return isDif
    }


    private fun isStairs(block: Block): Boolean {
        return block.identifier.endsWith("_stairs")
    }

    enum class Axis {
        X,
        Y,
        Z
    }

    fun rotateYCounterclockwise(direction: Direction): Direction {
        return when (direction.ordinal) {
            2 -> Direction.WEST
            5 -> Direction.NORTH
            3 -> Direction.EAST
            4 -> Direction.SOUTH
            else -> throw IllegalStateException("not supported rotation")
        }
    }

    fun rotateYClockwise(direction: Direction): Direction {
        return when (direction.ordinal) {
            2 -> Direction.EAST
            5 -> Direction.SOUTH
            3 -> Direction.WEST
            4 -> Direction.NORTH
            else -> throw IllegalStateException("not supported rotation")
        }
    }
}