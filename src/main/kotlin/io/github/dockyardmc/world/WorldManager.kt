package io.github.dockyardmc.world

import cz.lukynka.prettylog.AnsiPair
import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.profiler.profiler
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.registry.registries.DimensionType
import io.github.dockyardmc.world.generators.VoidWorldGenerator
import io.github.dockyardmc.world.generators.WorldGenerator
import java.util.concurrent.CompletableFuture

object WorldManager {

    val worlds: MutableMap<String, World> = mutableMapOf()
    val mainWorld: World = World("main", VoidWorldGenerator(Biomes.PLAINS), DimensionTypes.OVERWORLD)

    val LOG_TYPE = CustomLogType("\uD83C\uDF0E World Manager", AnsiPair.PURPLE)

    fun loadDefaultWorld() {
        profiler("Load default world") {
            mainWorld.load().thenAccept {
                worlds["main"] = mainWorld
                generateDefaultStonePlatform(mainWorld)
            }
        }
    }

    fun generateDefaultStonePlatform(world: World, size: Int = 30) {
        try {

            val centerX = (size - 1) / 2
            val centerZ = (size - 1) / 2

            world.defaultSpawnLocation = Location(centerX + 0.5, 1.0, centerZ + 0.5, world)

            for (x in 0 until size) {
                for (z in 0 until size) {
                    world.setBlock(x, 0, z, Blocks.STONE)
                }
            }

        } catch (ex: Exception) {
            log(ex)
        }
    }

    fun create(name: String, generator: WorldGenerator, dimensionType: DimensionType): World {
        require(!worlds.keys.contains(name)) { "World with name $name already exists!" }

        val world = World(name, generator, dimensionType)
        world.load()
        worlds[name] = world
        return world
    }

    fun createWithFuture(name: String, generator: WorldGenerator, dimensionType: DimensionType): CompletableFuture<World> {
        require(!worlds.keys.contains(name)) { "World with name $name already exists!" }

        val world = World(name, generator, dimensionType)
        worlds[name] = world
        return world.load().thenApply { world }
    }

    fun delete(name: String) {
        val world = getOrThrow(name)
        delete(world)
    }

    fun delete(world: World) {
        world.dispose()
    }

    fun getOrThrow(world: String): World = worlds[world] ?: throw IllegalArgumentException("World with name $world does not exist!")
}