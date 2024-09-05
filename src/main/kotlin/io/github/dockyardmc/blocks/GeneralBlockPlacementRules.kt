package io.github.dockyardmc.blocks

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks

object GeneralBlockPlacementRules {

    fun canBePlaced(originalClickedBlock: Location, where: Location, newBlock: Block, placer: Player): Boolean {

        var canBePlaced = CancelReason(true, "")

        val world = originalClickedBlock.world

        val existingBlock = world.getBlock(originalClickedBlock)
        val placementLocation = world.getBlock(where)

        if(placementLocation != Blocks.AIR && placementLocation != Blocks.LIGHT) canBePlaced = CancelReason(false, "Block at new location is not air")
        if(isLocationInsideBoundingBox(where, placer.world.entities.values) && newBlock.boundingBox == "block") canBePlaced = CancelReason(false, "Block collides with entity")
        if(world.getBlock(originalClickedBlock).boundingBox != "block") canBePlaced = CancelReason(false, "Block is not full block")
        if(existingBlock.isClickable && !placer.isSneaking) canBePlaced = CancelReason(false, "Block is clickable and player is not sneaking")

        return canBePlaced.canBePlaced
    }

    data class CancelReason(
        var canBePlaced: Boolean,
        var reason: String
    )

    //TODO Update entities to be stored inside chunk instead of all in the world and then check only the entities in the chunk
    private fun isLocationInsideBoundingBox(location: Location, entities: Collection<Entity>, toleranceY: Double = 0.2): Boolean {
        for (entity in entities) {
            val entityBoundingBox = entity.calculateBoundingBox()
            val insideX = entityBoundingBox.maxX > location.x && entityBoundingBox.minX < location.x + 1
            val insideZ = entityBoundingBox.maxZ > location.z && entityBoundingBox.minZ < location.z + 1
            val insideY = location.y >= entityBoundingBox.minY && location.y <= entityBoundingBox.maxY + toleranceY

            if (insideX && insideZ && insideY) {
                return true
            }
        }
        return false
    }
}