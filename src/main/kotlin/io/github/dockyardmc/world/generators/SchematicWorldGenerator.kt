package io.github.dockyardmc.world.generators

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.registries.Biome
import io.github.dockyardmc.schematics.Schematic
import io.github.dockyardmc.world.World

class SchematicWorldGenerator(val schematic: Schematic, val origin: Vector3 = Vector3(), defaultBiome: Biome = Biomes.PLAINS) : VoidWorldGenerator(defaultBiome) {
    override fun onWorldLoad(world: World) {
        world.placeSchematic(schematic, Location(origin, 0f, 0f, world))
    }
}