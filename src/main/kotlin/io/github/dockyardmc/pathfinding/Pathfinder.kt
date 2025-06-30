package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.pathing.Pathfinder
import de.metaphoriker.pathetic.api.pathing.configuration.PathfinderConfiguration
import de.metaphoriker.pathetic.api.pathing.configuration.PathfinderConfiguration.PathfinderConfigurationBuilder
import de.metaphoriker.pathetic.api.pathing.configuration.PathfinderConfiguration.builder
import de.metaphoriker.pathetic.engine.factory.AStarPathfinderFactory

object Pathfinder {

    private val AStarPathfinderFactory = AStarPathfinderFactory()
    private val initializer = PathfinderInitializer()

    val defaultPathfinder = createPathfinder {
        async(true)
        provider(PathfinderNavigationPointProvider())
        fallback(false)
    }

    fun createPathfinder(config: PathfinderConfiguration): Pathfinder {
        return AStarPathfinderFactory.createPathfinder(config, initializer)
    }

    inline fun createPathfinder(configuration: PathfinderConfigurationBuilder.() -> Unit): Pathfinder {
        val builder = builder()
        configuration.invoke(builder)
        builder.provider(PathfinderNavigationPointProvider())

        return createPathfinder(builder.build())
    }
}