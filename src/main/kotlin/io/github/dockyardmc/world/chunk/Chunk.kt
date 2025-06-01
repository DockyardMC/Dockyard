package io.github.dockyardmc.world.chunk

import io.github.dockyardmc.extentions.put
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundChunkDataPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUnloadChunkPacket
import io.github.dockyardmc.registry.registries.Biome
import io.github.dockyardmc.utils.viewable.Viewable
import io.github.dockyardmc.world.Light
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block
import io.github.dockyardmc.world.block.BlockEntity
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.jglrxavpok.hephaistos.collections.ImmutableLongArray
import org.jglrxavpok.hephaistos.nbt.NBT
import java.util.*

class Chunk(val chunkX: Int, val chunkZ: Int, val world: World) : Viewable() {

    companion object {
        private const val SECTION_BITS = 4
        private const val SECTION_MASK = 15
    }

    val id: UUID = UUID.randomUUID()
    val minSection = world.dimensionType.minY / 16
    val maxSection = world.dimensionType.height / 16
    private lateinit var cachedPacket: ClientboundChunkDataPacket
    override var autoViewable: Boolean = true

    val heightmaps = EnumMap<_, ChunkHeightmap>(ChunkHeightmap.Type::class.java)

    val motionBlocking: ImmutableLongArray = ImmutableLongArray(37) { 0 }
    val worldSurface: ImmutableLongArray = ImmutableLongArray(37) { 0 }

    val sections: MutableList<ChunkSection> = mutableListOf()
    val blockEntities: Int2ObjectOpenHashMap<BlockEntity> = Int2ObjectOpenHashMap(0)

    val light: Light = Light()

    val packet: ClientboundChunkDataPacket
        get() {
            if (!this::cachedPacket.isInitialized) updateCache()
            return cachedPacket
        }

    fun updateCache() {
        val heightmapNbt = NBT.Compound { builder ->
            heightmaps.forEach { map ->
                if (map.key.sendToClient()) {
                    builder.put(map.key.name, map.value.getRawData())
                }
            }
        }

        cachedPacket = ClientboundChunkDataPacket(chunkX, chunkZ, heightmapNbt, sections, blockEntities.values, light)
    }

    init {
        val sectionsAmount = maxSection - minSection
        repeat(sectionsAmount) {
            sections.add(ChunkSection.empty())
        }
        ChunkHeightmap.Type.entries.forEach { type ->
            getOrCreateHeightmap(type)
            ChunkHeightmap.generate(this, setOf(type))
        }
        updateCache()
    }

    fun setBlockRaw(x: Int, y: Int, z: Int, blockStateId: Int, shouldCache: Boolean = true) {
        val section = getSectionAt(y)
        val relativeX = ChunkUtils.sectionRelative(x)
        val relativeZ = ChunkUtils.sectionRelative(z)
        val relativeY = ChunkUtils.sectionRelative(y)

        section.setBlock(relativeX, relativeY, relativeZ, blockStateId)
        world.customDataBlocks.remove(Location(x, y, z, world).blockHash)

        val block = Block.getBlockByStateId(blockStateId)
        heightmaps.getValue(ChunkHeightmap.Type.MOTION_BLOCKING).update(relativeX, relativeY, relativeZ, block)
        heightmaps.getValue(ChunkHeightmap.Type.MOTION_BLOCKING_NO_LEAVES).update(relativeX, relativeY, relativeZ, block)
        heightmaps.getValue(ChunkHeightmap.Type.OCEAN_FLOOR).update(relativeX, relativeY, relativeZ, block)
        heightmaps.getValue(ChunkHeightmap.Type.WORLD_SURFACE).update(relativeX, relativeY, relativeZ, block)

        if (shouldCache) updateCache()
    }

    fun setBiome(x: Int, y: Int, z: Int, biome: Biome, shouldCache: Boolean = true) {
        val section = getSectionAt(y)

        val relativeX = ChunkUtils.sectionRelative(x)
        val relativeZ = ChunkUtils.sectionRelative(z)
        val relativeY = ChunkUtils.sectionRelative(y)

        section.setBiome(relativeX, relativeY, relativeZ, biome.getProtocolId())
        if (shouldCache) updateCache()
    }

    fun setBlock(x: Int, y: Int, z: Int, block: Block, shouldCache: Boolean = true) {
        val section = getSectionAt(y)

        val relativeX = ChunkUtils.sectionRelative(x)
        val relativeZ = ChunkUtils.sectionRelative(z)
        val relativeY = ChunkUtils.sectionRelative(y)

        if (block.customData != null) world.customDataBlocks[Location(x, y, z, world).blockHash] = block
        if (block.customData == null) world.customDataBlocks.remove(Location(x, y, z, world).blockHash)
        section.setBlock(relativeX, relativeY, relativeZ, block.getProtocolId())

        heightmaps.getValue(ChunkHeightmap.Type.MOTION_BLOCKING).update(relativeX, y, relativeZ, block)
        heightmaps.getValue(ChunkHeightmap.Type.MOTION_BLOCKING_NO_LEAVES).update(relativeX, y, relativeZ, block)
        heightmaps.getValue(ChunkHeightmap.Type.OCEAN_FLOOR).update(relativeX, y, relativeZ, block)
        heightmaps.getValue(ChunkHeightmap.Type.WORLD_SURFACE).update(relativeX, y, relativeZ, block)

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

        val id = section.getBlock(relativeX, relativeY, relativeZ)
        return Block.getBlockByStateId(id)
    }

    fun fillBiome(biome: Biome) {
        sections.forEach { section ->
            section.fillBiome(biome.getProtocolId())
        }
    }

    fun fillBlocks(block: Block) {
        sections.forEach { section ->
            section.fillBlock(block.getProtocolId())
        }
    }

    fun getSection(section: Int): ChunkSection = sections[section - minSection]

    fun getSectionAt(y: Int): ChunkSection = getSection(ChunkUtils.getChunkCoordinate(y))

    fun getIndex(): Long = ChunkUtils.getChunkIndex(this)

    val chunkPos get() = ChunkPos(chunkX, chunkZ)

    fun highestNonEmptySectionIndex(): Int? {
        for (i in sections.size - 1 downTo 0) {
            val section = sections[i]
            if (!section.hasOnlyAir()) return i
        }
        return null
    }

    fun highestSectionY(): Int {
        val highestSectionIndex = highestNonEmptySectionIndex() ?: return world.dimensionType.minY
        return getSectionYFromSectionIndex(highestSectionIndex)
    }

    fun getSectionYFromSectionIndex(index: Int): Int {
        return index + world.dimensionType.minY shr SECTION_BITS
    }

    fun getOrCreateHeightmap(type: ChunkHeightmap.Type): ChunkHeightmap = heightmaps.computeIfAbsent(type) { ChunkHeightmap(this, type) }

    fun sendUpdateToViewers() {
        viewers.sendPacket(cachedPacket)
    }

    override fun addViewer(player: Player): Boolean {
        if (!super.addViewer(player)) return false
        player.sendPacket(cachedPacket)
        return true
    }

    override fun removeViewer(player: Player) {
        super.removeViewer(player)
        player.sendPacket(ClientboundUnloadChunkPacket(this.chunkPos))
    }

    override fun toString(): String {
        return "Chunk(${chunkPos.x}, ${chunkPos.z}, ${world.name})"
    }
}