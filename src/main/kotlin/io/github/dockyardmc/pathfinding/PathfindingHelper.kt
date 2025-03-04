package io.github.dockyardmc.pathfinding

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.world.block.Block

object PathfindingHelper {

    val nonSolidBlocksThatShouldBeCountedAsSolid: List<String> = mutableListOf("minecraft:fences")
    val solidBlocksThatShouldBeCountedAsNonSolid: List<String> = mutableListOf()

    fun isTraversable(block: Block, location: Location): Boolean {
        val registryBlock = block.registryBlock

        if(registryBlock.isAir) return false

        if (registryBlock.tags.contains("minecraft:fence_gates")) {
            return block.blockStates["open"] != "true"
        }

        var isSolid = registryBlock.isSolid

        if (registryBlock.tags.contains("minecraft:trapdoors")) {
            isSolid = block.blockStates["open"] != "true"
            if(block.blockStates["half"] == "bottom") isSolid = false
        }

        if(registryBlock.tags.contains("minecraft:pressure_plates")) {
            isSolid = false
        }

        if (registryBlock.tags.contains("minecraft:doors")) {
            isSolid = block.blockStates["open"] != "true"
        }

        if(registryBlock.identifier == "minecraft:sculk_vein") isSolid = false

        if(registryBlock.identifier == "minecraft:snow") {
            val layers = block.blockStates["layers"]?.toInt()
            if(layers != null) {
                isSolid = layers >= 4
            }
        }

        nonSolidBlocksThatShouldBeCountedAsSolid.forEach { tag ->
            if (registryBlock.tags.contains(tag)) {
                isSolid = true
            }
        }

        solidBlocksThatShouldBeCountedAsNonSolid.forEach { identifier ->
            if(registryBlock.identifier == identifier) {
                isSolid = false
            }
        }

        return isSolid
    }
}