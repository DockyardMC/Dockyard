package io.github.dockyardmc.world

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.WorldFinishLoadingEvent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.main
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.DimensionType
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.world.generators.VoidWorldGenerator
import io.github.dockyardmc.world.generators.WorldGenerator

object WorldManager {

    val worlds: MutableMap<String, World> = mutableMapOf()
    val mainWorld = create("main", VoidWorldGenerator(), DimensionTypes.OVERWORLD)

    init {
        Events.on<WorldFinishLoadingEvent> {
            if(it.world == mainWorld) generateStonePlatform(mainWorld)
        }
    }

    private fun generateStonePlatform(world: World) {
        val platformSize = 30

        val centerX = (platformSize - 1) / 2
        val centerZ = (platformSize - 1) / 2

        world.defaultSpawnLocation = Location(centerX + 0.5, 1.0, centerZ + 0.5, world)

        for (x in 0 until platformSize) {
            for (z in 0 until platformSize) {
                world.setBlock(x, 0, z, Blocks.STONE)
            }
        }
    }

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