package io.github.dockyardmc.pathfinding

import de.metaphoriker.pathetic.api.pathing.Pathfinder
import de.metaphoriker.pathetic.api.pathing.configuration.PathfinderConfiguration
import de.metaphoriker.pathetic.api.pathing.configuration.PathfinderConfiguration.PathfinderConfigurationBuilder
import de.metaphoriker.pathetic.engine.factory.AStarPathfinderFactory

object Pathfinder {

    private val AStarPathfinderFactory = AStarPathfinderFactory()
    private val initializer = PathfinderInitializer()

    val defaultPathfinder = createPathfinder {
        async(true)
        provider(PathfinderNavigationPointProvider())
        fallback(false)
    }

    private fun getConfig(configuration: PathfinderConfigurationBuilder.() -> Unit): PathfinderConfiguration {
        val builder = PathfinderConfiguration.builder()
        configuration.invoke(builder)
        builder.provider(PathfinderNavigationPointProvider())
        return builder.build()
    }

    fun createPathfinder(configuration: PathfinderConfigurationBuilder.() -> Unit): Pathfinder {
        val config = getConfig(configuration)
        return AStarPathfinderFactory.createPathfinder(config, initializer)
    }
}