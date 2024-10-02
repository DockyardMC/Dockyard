package io.github.dockyardmc.blocks

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.debug

object GeneralBlockPlacementRules {

    fun canBePlaced(originalClickedBlock: Location, where: Location, newBlock: Block, placer: Player): Boolean {

        var canBePlaced = CancelReason(true, "")

        val world = originalClickedBlock.world

        val clickedBlock = world.getBlock(originalClickedBlock)
        val placementLocation = world.getBlock(where)

        if(!placementLocation.isAir() && placementLocation.registryBlock != Blocks.LIGHT) canBePlaced = CancelReason(false, "Block at new location is not air (${placementLocation.identifier})")
        if(isLocationInsideBoundingBox(where, placer.world.entities.values) && newBlock.registryBlock.isSolid) canBePlaced = CancelReason(false, "Block collides with entity")
        if(!clickedBlock.registryBlock.isSolid) canBePlaced = CancelReason(false, "Block is not full block (${clickedBlock.identifier})")
//        if(existingBlock && !placer.isSneaking) canBePlaced = CancelReason(false, "Block is clickable and player is not sneaking")

        if(!canBePlaced.canBePlaced) debug(canBePlaced.reason, true)

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