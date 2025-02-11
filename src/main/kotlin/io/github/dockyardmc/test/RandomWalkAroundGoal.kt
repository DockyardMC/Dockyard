package io.github.dockyardmc.test

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ai.AIGoal
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.Navigator
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard.toPathPosition
import io.github.dockyardmc.pathfinding.PathfindingHelper
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.utils.randomFloat
import io.github.dockyardmc.utils.randomInt

class RandomWalkAroundGoal(override var entity: Entity, override var priority: Int, val navigator: Navigator) : AIGoal() {

    val chancePerTick = 35
    val startingLocation = entity.location
    var hasFinishedWalking = false

    override fun startCondition(): Boolean {
        return randomInt(chancePerTick, 100) == chancePerTick
    }

    override fun start() {
        hasFinishedWalking = false
        var locationToPathfindTo: Location? = null
        startingLocation.getBlocksInRadius(10).shuffled().forEach { location ->
            if(!PathfindingHelper.isTraversable(location.block, location)) return@forEach
            val start = entity.location.getBlockLocation().subtract(0, 1, 0).toPathPosition()
            val end = location.toPathPosition()

            navigator.pathfinder.findPath(start, end, navigator.filters).thenAccept { result ->
                if(!result.successful()) return@thenAccept
                locationToPathfindTo = location
            }
        }

        if(locationToPathfindTo == null) {
            hasFinishedWalking = true
            return
        }

        navigator.updatePathfindingPath(locationToPathfindTo!!)
        navigator.onNavigationNodeStep {
            entity.playSoundToViewers(Sound(Sounds.ENTITY_RAVAGER_STEP, volume = 0.1f, pitch = randomFloat(1f, 1.3f)), entity.location)
        }
        navigator.onNavigationComplete {
            hasFinishedWalking = true
        }
    }

    override fun end() {
        navigator.cancelNavigating()
        hasFinishedWalking = false
    }

    override fun endCondition(): Boolean {
        return hasFinishedWalking
    }

    override fun tick() {

    }

}

fun Location.getBlocksInRadius(radius: Int): List<Location> {
    val x = this.x
    val y = this.y
    val z = this.z

    val locations = mutableListOf<Location>()

    for (dx in -radius..radius) {
        for (dy in -radius..radius) {
            for (dz in -radius..radius) {
                val blockLocation = Location(x + dx, y + dy, z + dz, this.world)
                locations.add(blockLocation)
            }
        }
    }

    return locations.toList()
}