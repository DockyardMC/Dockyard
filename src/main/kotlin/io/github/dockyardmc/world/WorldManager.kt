package io.github.dockyardmc.world

import io.github.dockyardmc.extentions.addIfNotPresent
import io.github.dockyardmc.registry.DimensionType
import io.github.dockyardmc.world.generators.WorldGenerator

object WorldManager {

    val worlds: MutableList<World> = mutableListOf()

    fun create(name: String, generator: WorldGenerator, dimensionType: DimensionType): World {
        require(worlds.firstOrNull { it.name == name } == null) { "World with that name already exists!" }
        val world = World(name, generator, dimensionType)

        worlds.addIfNotPresent(world)
        return world
    }
}