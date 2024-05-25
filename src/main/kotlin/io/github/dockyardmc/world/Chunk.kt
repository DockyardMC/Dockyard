package io.github.dockyardmc.world

import java.util.UUID

class Chunk(val chunkX: Int, val chunkZ: Int, val world: World) {

    val id: UUID = UUID.randomUUID()
    val minSection = world.dimensionType.minY / 16
    val maxSection = (world.dimensionType.minY + world.dimensionType.maxY)/ 16

    val sections: MutableList<ChunkSection> = mutableListOf()

    init {
        val sectionsAmount = maxSection - minSection
        repeat(sectionsAmount) {
            sections.add(ChunkSection.empty())
        }
    }
}