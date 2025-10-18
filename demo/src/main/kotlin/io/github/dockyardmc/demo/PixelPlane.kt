package io.github.dockyardmc.demo

import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.entities.CopperGolem
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector2
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import java.util.concurrent.CompletableFuture

data class PixelPlane(val height: Int, val width: Int) {
    val startingPoint = Location(0, 0, 0, WorldManager.mainWorld)
    val endingPoint = Location(0 + height, 0, 0 + width, WorldManager.mainWorld)

    val golems = mutableMapOf<Clanker, Vector2>()

    fun spawnPlatform(): CompletableFuture<World> {
        return WorldManager.mainWorld.batchBlockUpdate {
            fill(startingPoint, endingPoint, Blocks.BLACK_CONCRETE)
        }
    }

    fun spawnGolems() {

        val minX = minOf(startingPoint.x, endingPoint.x)
        val minZ = minOf(startingPoint.z, endingPoint.z)

        val maxX = maxOf(startingPoint.x, endingPoint.x)
        val maxZ = maxOf(startingPoint.z, endingPoint.z)

        for (iX in minX.toInt()..maxX.toInt()) {
            for (iZ in minZ.toInt()..maxZ.toInt()) {
                val location = Location(iX, (startingPoint.y.toInt() + 1), iZ, WorldManager.mainWorld)
                golems[Clanker(startingPoint.add(0, 1, 0).add(0.5, 0.0, 0.5), location)] = Vector2(iX, iZ)
            }
        }

        var i = 0
        WorldManager.mainWorld.scheduler.repeat(golems.size, 2.ticks) {
            i++
            val golem = golems.keys.toList()[i]
            val entity = WorldManager.mainWorld.spawnEntity<Clanker>(golem)
            entity.weatherState.value = CopperGolem.WeatherState.OXIDIZED
        }
    }
}