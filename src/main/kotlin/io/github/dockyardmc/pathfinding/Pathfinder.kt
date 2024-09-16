package io.github.dockyardmc.pathfinding

import io.github.dockyardmc.entities.BlockDisplay
import io.github.dockyardmc.entities.DisplayEntityBase
import io.github.dockyardmc.entities.EntityManager.despawnEntity
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.entities.TextDisplay
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.Blocks

data class Node(
    val location: Location,
    val costToGoal: Double
)

class Pathfinder(
    val goal: Location,
    val start: Location,
) {
    val visualizer = mutableListOf<DisplayEntityBase>()
    val traversed = mutableListOf<Location>()
    val unknown = mutableListOf<Node>()

    var nearNodes = mutableListOf<Node>()
    var current: Node = locationToNode(start)

    fun findPath() {

    }

    fun nextStep() {
        val adjacent = current.location.getAdjacentLocations()
        nearNodes = adjacent.map { locationToNode(it) }.toMutableList()
        nearNodes.sortedBy { it.costToGoal }.forEach {
            updateDisplays(nearNodes)
            if(it.location.world.getBlock(it.location).identifier == Blocks.AIR.identifier) return@forEach
            if(traversed.hashedContains(it.location)) return@forEach
            if(it.location.y < current.location.y) return@forEach

            traversed.add(current.location)
            current = it
            return
        }
    }

    fun updateDisplays(nearNodes: MutableList<Node>) {
        visualizer.forEach { display -> display.world.despawnEntity(display) }
        visualizer.forEach { display -> display.world.despawnEntity(display) }

        val currentDisplay = current.location.world.spawnEntity(BlockDisplay(current.location.add(0.25, 0.0, 0.25))) as BlockDisplay
        currentDisplay.block.value = Blocks.LIME_CONCRETE
        currentDisplay.scaleTo(0.6f)
        currentDisplay.translateTo(0f, 0.55f, 0f)

        visualizer.add(currentDisplay)

        nearNodes.forEach {

            if(it.location.getBlock().identifier == Blocks.AIR.identifier) return@forEach

            val blockDisplay = it.location.world.spawnEntity(BlockDisplay(it.location.add(0.25, 0.0, 0.25))) as BlockDisplay
            blockDisplay.block.value = Blocks.LIGHT_BLUE_CONCRETE
            blockDisplay.scaleTo(0.5f)
            blockDisplay.translateTo(0f, 0.52f, 0f)
            val textDisplay = it.location.world.spawnEntity(TextDisplay(it.location.add(0.5, 1.2, 0.5))) as TextDisplay
            textDisplay.text.value = it.costToGoal.truncate(2)

            blockDisplay.autoViewable = true
            textDisplay.autoViewable = true

            visualizer.add(blockDisplay)
            visualizer.add(textDisplay)
        }

        traversed.forEach {

            val blockDisplay = it.world.spawnEntity(BlockDisplay(it.add(0.25, 0.0, 0.25))) as BlockDisplay
            blockDisplay.block.value = Blocks.RED_CONCRETE
            blockDisplay.scaleTo(0.5f)
            blockDisplay.translateTo(0f, 0.52f, 0f)

            blockDisplay.autoViewable = true
            visualizer.add(blockDisplay)
        }
    }

    fun locationToNode(location: Location): Node {
        return Node(location, location.distance(goal))
    }
}

fun MutableList<Location>.hashedContains(location: Location): Boolean {
    val containsHash = "${location.blockX}${location.blockY}${location.blockZ}"
    val mapped = this.map { "${it.blockX}${it.blockY}${it.blockZ}" }
    return mapped.contains(containsHash)
}