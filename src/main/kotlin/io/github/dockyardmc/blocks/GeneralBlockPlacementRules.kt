package io.github.dockyardmc.blocks

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.world.WorldManager

object GeneralBlockPlacementRules {

    fun canBePlaced(originalClickedBlock: Location, where: Location, newBlock: Block, placer: Player): Boolean {

        var canBePlaced = CancelReason(true, "")

        val world = WorldManager.worlds[0]

        val existingBlock = world.getBlock(originalClickedBlock)
        val placementLocation = world.getBlock(where)

        if(placementLocation != Blocks.AIR) canBePlaced = CancelReason(false, "Block at new location is not air")
        if(isLocationInsideBoundingBox(where, WorldManager.worlds[0].entities) && newBlock.boundingBox == "block") canBePlaced = CancelReason(false, "Block collides with entity")
        if(WorldManager.worlds[0].getBlock(originalClickedBlock).boundingBox != "block") canBePlaced = CancelReason(false, "Block is not full block")
        if(existingBlock.isClickable && !placer.isSneaking) canBePlaced = CancelReason(false, "Block is clickable and player is not sneaking")

//        repeat(10) {
//            DockyardServer.broadcastMessage(" ")
//        }
//        DockyardServer.broadcastMessage("<gray>existing: <lime>${existingBlock.name}")
//        DockyardServer.broadcastMessage("<gray>new location: <yellow>${placementLocation.name}")
//        DockyardServer.broadcastMessage("<gray>existing location bounding box: <white>${existingBlock.boundingBox}")
//        DockyardServer.broadcastMessage("<gray>new location boundingBox: <white>${placementLocation.boundingBox}")
//        DockyardServer.broadcastMessage("<gray>actual is clickable: <white>${existingBlock.isClickable}")
//        DockyardServer.broadcastMessage("<gray>new location is clickable: <white>${placementLocation.isClickable}")
//        DockyardServer.broadcastMessage(" ")
//        DockyardServer.broadcastMessage("<gray>new block is clickable: <aqua>${newBlock.isClickable}")
//        DockyardServer.broadcastMessage("<gray>new block bounding box: <aqua>${newBlock.boundingBox}")


//        if(!canBePlaced.canBePlaced) DockyardServer.broadcastMessage("<red>${canBePlaced.reason}")
        return canBePlaced.canBePlaced
    }

    data class CancelReason(
        var canBePlaced: Boolean,
        var reason: String
    )

    //TODO Update entities to be stored inside chunk instead of all in the world and then check only the entities in the chunk
    fun isLocationInsideBoundingBox(location: Location, entities: List<Entity>, toleranceY: Double = 0.2): Boolean {
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