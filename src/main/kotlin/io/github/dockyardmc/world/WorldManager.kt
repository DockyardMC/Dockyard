package io.github.dockyardmc.world

import io.github.dockyardmc.registry.DimensionType
import io.github.dockyardmc.world.generators.WorldGenerator

object WorldManager {

    val worlds: MutableMap<String, World> = mutableMapOf()

    fun create(name: String, generator: WorldGenerator, dimensionType: DimensionType): World {
        require(!worlds.keys.contains(name)) { "World with name $name already exists!" }

        val world = World(name, generator, dimensionType)

        worlds[name] = world
        return world
    }

    fun delete(name: String) {
        val world = getOrThrow(name)
        delete(world)
    }

    fun delete(world: World) {
        world.delete()
    }

    fun getOrThrow(world: String): World = worlds[world] ?: throw Exception("World with name $world does not exist!")
}