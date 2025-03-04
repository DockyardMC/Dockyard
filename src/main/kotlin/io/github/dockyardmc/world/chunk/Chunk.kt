package io.github.dockyardmc.world.chunk

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundChunkDataPacket
import io.github.dockyardmc.registry.registries.Biome
import io.github.dockyardmc.utils.ChunkUtils
import io.github.dockyardmc.world.Light
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block
import io.github.dockyardmc.world.block.BlockEntity
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.jglrxavpok.hephaistos.collections.ImmutableLongArray
import org.jglrxavpok.hephaistos.nbt.NBT
import java.util.*

class Chunk(val chunkX: Int, val chunkZ: Int, val world: World) {

    val id: UUID = UUID.randomUUID()
    val minSection = world.dimensionType.minY / 16
    val maxSection = world.dimensionType.height / 16
    private lateinit var cachedPacket: ClientboundChunkDataPacket

    val motionBlocking: ImmutableLongArray = ImmutableLongArray(37) { 0 }
    val worldSurface: ImmutableLongArray = ImmutableLongArray(37) { 0 }

    val sections: MutableList<ChunkSection> = mutableListOf()
    val blockEntities: Int2ObjectOpenHashMap<BlockEntity> = Int2ObjectOpenHashMap(0)

    val light: Light = Light(
        skyLight = ByteArray(0),
        blockLight = ByteArray(0),
    )

    val packet: ClientboundChunkDataPacket
        get() {
            if (!this::cachedPacket.isInitialized) updateCache()
            return cachedPacket
        }

    fun updateCache() {
        val heightMap = NBT.Compound {
            it.put("MOTION_BLOCKING", NBT.LongArray(motionBlocking))
            it.put("WORLD_SURFACE", NBT.LongArray(worldSurface))
        }
        cachedPacket = ClientboundChunkDataPacket(chunkX, chunkZ, heightMap, sections, blockEntities.values, light)
    }

    init {
        val sectionsAmount = maxSection - minSection
        repeat(sectionsAmount) {
            sections.add(ChunkSection.empty())
        }
        updateCache()
    }

    fun setBlockRaw(x: Int, y: Int, z: Int, blockStateId: Int, shouldCache: Boolean = true) {
        val section = getSectionAt(y)
        val relativeX = ChunkUtils.sectionRelative(x)
        val relativeZ = ChunkUtils.sectionRelative(z)
        val relativeY = ChunkUtils.sectionRelative(y)

        section.blockPalette[relativeX, relativeY, relativeZ] = blockStateId
        world.customDataBlocks.remove(Location(x, y, z, world).blockHash)

        if (shouldCache) updateCache()
    }

    fun setBiome(x: Int, y: Int, z: Int, biome: Biome, shouldCache: Boolean = true) {
        val section = getSectionAt(y)

        val relativeX = ChunkUtils.sectionRelative(x)
        val relativeZ = ChunkUtils.sectionRelative(z)
        val relativeY = ChunkUtils.sectionRelative(y)

        section.biomePalette[relativeX, relativeY, relativeZ] = biome.getProtocolId()
        if (shouldCache) updateCache()
    }

    fun setBlock(x: Int, y: Int, z: Int, block: Block, shouldCache: Boolean = true) {
        val section = getSectionAt(y)

        val relativeX = ChunkUtils.sectionRelative(x)
        val relativeZ = ChunkUtils.sectionRelative(z)
        val relativeY = ChunkUtils.sectionRelative(y)

        if (block.customData != null) world.customDataBlocks[Location(x, y, z, world).blockHash] = block
        if (block.customData == null) world.customDataBlocks.remove(Location(x, y, z, world).blockHash)
        section.blockPalette[relativeX, relativeY, relativeZ] = block.getProtocolId()

        val index = ChunkUtils.chunkBlockIndex(x, y, z)

        if (block.registryBlock.isBlockEntity) {
            val blockEntity = BlockEntity(index, block.registryBlock, NBT.Compound())
            blockEntities[index] = blockEntity
        } else {
            blockEntities.remove(index)
        }

        if (shouldCache) updateCache()
    }

    fun getBlock(x: Int, y: Int, z: Int): Block {
        val customDataBlock = world.customDataBlocks[(x.hashCode() + y.hashCode() + z.hashCode() + world.name.hashCode())]
        if (customDataBlock != null) return customDataBlock

        val section = getSectionAt(y)

        val relativeX = ChunkUtils.sectionRelative(x)
        val relativeZ = ChunkUtils.sectionRelative(z)
        val relativeY = ChunkUtils.sectionRelative(y)

        val id = section.blockPalette[relativeX, relativeY, relativeZ]
        return Block.getBlockByStateId(id) ?: throw IllegalStateException("Block state with id $id not found")
    }

    fun fillBiome(biome: Biome) {
        sections.forEach {
            it.biomePalette.fill(biome.getProtocolId())
        }
    }

    fun fillBlocks(block: Block) {
        sections.forEach {
            it.biomePalette.fill(block.getProtocolId())
        }
    }

    fun getSection(section: Int): ChunkSection = sections[section - minSection]

    fun getSectionAt(y: Int): ChunkSection = getSection(ChunkUtils.getChunkCoordinate(y))

    fun getIndex(): Long = ChunkUtils.getChunkIndex(this)

    val chunkPos get() = ChunkPos(chunkX, chunkZ)
}