package io.github.dockyardmc.world.block

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Blocks

object GeneralBlockPlacementRules {

    fun canBePlaced(originalClickedBlock: Location, where: Location, newBlock: Block, placer: Player): CancelReason {

        var canBePlaced = CancelReason(true, "")

        val world = originalClickedBlock.world

        val clickedBlock = world.getBlock(originalClickedBlock)
        val placementLocation = world.getBlock(where)
        val blockBelow = where.subtract(0, 1, 0).block

        if (!placementLocation.isAir() && placementLocation.registryBlock != Blocks.LIGHT && !placementLocation.registryBlock.isLiquid) canBePlaced = CancelReason(false, "Block at new location is not air (${placementLocation.identifier})")

        if (isLocationInsideBoundingBox(
                where,
                placer.world.entities
            ) && newBlock.registryBlock.isSolid
        ) canBePlaced = CancelReason(false, "Block collides with entity")
        if (!blockBelow.registryBlock.isSolid) canBePlaced =
            CancelReason(false, "Block is not full block (${blockBelow.identifier})")
        if (BlockDataHelper.isClickable(clickedBlock) && !placer.isSneaking) canBePlaced =
            CancelReason(false, "Block is clickable and player is not sneaking")

        return canBePlaced
    }

    data class CancelReason(
        var canBePlaced: Boolean,
        var reason: String,
    )

    //TODO Update entities to be stored inside chunk instead of all in the world and then check only the entities in the chunk
    private fun isLocationInsideBoundingBox(
        location: Location,
        entities: Collection<Entity>,
        toleranceY: Double = 0.2,
    ): Boolean {
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