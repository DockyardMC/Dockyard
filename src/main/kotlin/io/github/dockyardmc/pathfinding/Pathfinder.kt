package io.github.dockyardmc.pathfinding

import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.CommandException
import io.github.dockyardmc.entities.BlockDisplay
import io.github.dockyardmc.entities.DisplayEntityBase
import io.github.dockyardmc.entities.EntityManager.despawnEntity
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.entities.TextDisplay
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.isEmpty
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.runnables.timedSequenceAsync
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.MathUtils
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class Node(
    val location: Location,
    val costToGoal: Double
)

class Pathfinder(
    val goal: Location,
    val start: Location,
) {
    val visualizer = mutableListOf<DisplayEntityBase>()
    val traversed = mutableListOf<Location>(start)
    var discovered = mutableListOf<Node>()

    var nearNodes = mutableListOf<Node>()
    var current: Node = locationToNode(start)

    val currentPath = mutableListOf<Node>()

    fun nextStep() {
        val adjacent = current.location.getAdjacentLocations()
        nearNodes = adjacent.map { locationToNode(it) }.toMutableList()
        val possibleNodes = mutableListOf<Node>()
        nearNodes.sortedBy { it.costToGoal }.forEach {
            if(it.location.world.getBlock(it.location).isEmpty()) return@forEach
            if(traversed.hashedContains(it.location)) return@forEach
            if(it.location.y < current.location.y) return@forEach
            if(!it.location.getAdjacentLocations().any { adj -> adj.getBlock().isEmpty() || traversed.hashedContains(adj) }) return@forEach
            possibleNodes.add(it)
        }
        updateDisplays(possibleNodes)


        val next = possibleNodes.minByOrNull { it.costToGoal + heuristic(it.location) }
        if(next == null) {
            log("Cant find more nodes to go")
            if(discovered.isEmpty()) throw CommandException("Cant find any possible path!")

            // can backtrack to a discovered nodes
            throw CommandException("cant find any path, backtracking possible")
        }

        if(current.location.equalsBlock(goal)) {
            DockyardServer.broadcastMessage("<lime>reached goal!!")
            timedSequenceAsync { seq ->
                currentPath.map { it.location }.forEach { path ->
                    path.world.setBlock(path, Blocks.YELLOW_CONCRETE)
                    path.world.players.values.playSound("minecraft:entity.chicken.egg", pitch = MathUtils.randomFloat(1f, 1.6f))
                    seq.wait(2.ticks)
                }
            }
        }

        possibleNodes.remove(next)
        discovered.addAll(possibleNodes)
        traversed.add(next.location)
        discovered = discovered.filter { !traversed.hashedContains(it.location) }.toMutableList()
        current = next
        currentPath.add(next)
    }


    fun backTrack() {
        currentPath.removeLast()
        val prev = currentPath.last()
        current = prev
        nextStep()
    }

    fun updateDisplays(nearNodes: MutableList<Node>) {
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
            textDisplay.text.value = (it.costToGoal + heuristic(it.location)).truncate(6)

            blockDisplay.autoViewable = true
            textDisplay.autoViewable = true

            visualizer.add(blockDisplay)
            visualizer.add(textDisplay)
        }

        traversed.forEach {
            it.world.setBlock(it, Blocks.RED_CONCRETE)
        }

        discovered.forEach {
            it.location.world.setBlock(it.location, Blocks.PURPLE_CONCRETE)
        }
    }

    fun locationToNode(location: Location): Node {
        return Node(location, location.distance(goal))
    }

    fun heuristic(location: Location): Double {
        // Calculate Manhattan distance to the goal
        val distanceToGoal = location.distance(goal)

        // Calculate Manhattan distance to the starting point
        val distanceToStart = location.distance(start)

        // Calculate a decreasing penalty based on the distance to the start
        val penaltyFactor = 0.1 / (distanceToStart + 1) // Adjust the denominator as needed
        val backtrackingPenalty = penaltyFactor * distanceToStart

        return distanceToGoal + backtrackingPenalty
    }
}

fun MutableList<Location>.hashedContains(location: Location): Boolean {
    val containsHash = "${location.blockX}${location.blockY}${location.blockZ}"
    val mapped = this.map { "${it.blockX}${it.blockY}${it.blockZ}" }
    return mapped.contains(containsHash)
}

