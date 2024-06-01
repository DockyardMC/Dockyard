package io.github.dockyardmc.world

import io.github.dockyardmc.material.Material
import io.github.dockyardmc.protocol.CachedPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundChunkDataPacket
import io.github.dockyardmc.utils.ChunkUtils
import org.jglrxavpok.hephaistos.collections.ImmutableLongArray
import org.jglrxavpok.hephaistos.nbt.NBT
import java.util.UUID

class Chunk(val chunkX: Int, val chunkZ: Int, val world: World) {

    val id: UUID = UUID.randomUUID()
    val minSection = world.dimensionType.minY / 16
    val maxSection = (world.dimensionType.minY + world.dimensionType.maxY)/ 16
    private lateinit var cachedPacket: CachedPacket

    val motionBlocking: ImmutableLongArray = ImmutableLongArray(37) { 0 }
    val worldSurface: ImmutableLongArray = ImmutableLongArray(37) { 0 }

    val sections: MutableList<ChunkSection> = mutableListOf()

    val light: Light = Light(
        skyLight =  ByteArray(0),
        blockLight = ByteArray(0),
    )

    val packet: ClientboundChunkDataPacket get() {
        if(!this::cachedPacket.isInitialized || !this.cachedPacket.isValid) cacheChunkDataPacket()
        cacheChunkDataPacket() // always make new one idc
        return cachedPacket.packet as ClientboundChunkDataPacket
    }

    fun cacheChunkDataPacket() {
        val heightMap = NBT.Compound {
            it.put("MOTION_BLOCKING", NBT.LongArray(motionBlocking))
            it.put("WORLD_SURFACE", NBT.LongArray(worldSurface))
        }

        val packet = ClientboundChunkDataPacket(chunkX, chunkZ, heightMap, sections, light)
        cachedPacket = CachedPacket(true, packet)
    }

    init {
        val sectionsAmount = maxSection - minSection
        repeat(sectionsAmount) {
            sections.add(ChunkSection.empty())
        }
        cacheChunkDataPacket()
    }

    fun setBlock(x: Int, y: Int, z: Int, material: Material) {
        val section = getSectionAt(y)

        val relativeX = ChunkUtils.sectionRelative(x)
        val relativeZ = ChunkUtils.sectionRelative(z)
        val relativeY = ChunkUtils.sectionRelative(y)

        section.blockPalette.set(relativeX, relativeY, relativeZ, material.blockStateId)

    }

    fun getSection(section: Int): ChunkSection {
        return sections.get(section - minSection)
    }

    fun getSectionAt(y: Int): ChunkSection {
        return getSection(ChunkUtils.getChunkCoordinate(y))
    }

}