package io.github.dockyardmc.world.chunk

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.blocks.BlockEntity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundChunkDataPacket
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.Biome
import io.github.dockyardmc.utils.ChunkUtils
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.Vector3d
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.light.Light
import io.github.dockyardmc.world.light.LightEngine
import io.github.dockyardmc.world.light.LightLookup
import io.github.dockyardmc.world.light.PaletteLookup
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.jglrxavpok.hephaistos.collections.ImmutableLongArray
import org.jglrxavpok.hephaistos.nbt.NBT
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.max

class Chunk(val chunkX: Int, val chunkZ: Int, val world: World) {

    companion object {
        private val pool: ExecutorService = Executors.newWorkStealingPool()

        val DIFFUSE_SKY_LIGHT = setOf<String>(
            Blocks.COBWEB.identifier,
            Blocks.ICE.identifier,
            Blocks.HONEY_BLOCK.identifier,
            Blocks.SLIME_BLOCK.identifier,
            Blocks.WATER.identifier,
            Blocks.ACACIA_LEAVES.identifier,
            Blocks.AZALEA_LEAVES.identifier,
            Blocks.BIRCH_LEAVES.identifier,
            Blocks.DARK_OAK_LEAVES.identifier,
            Blocks.FLOWERING_AZALEA_LEAVES.identifier,
            Blocks.JUNGLE_LEAVES.identifier,
            Blocks.CHERRY_LEAVES.identifier,
            Blocks.OAK_LEAVES.identifier,
            Blocks.SPRUCE_LEAVES.identifier,
            Blocks.SPAWNER.identifier,
            Blocks.BEACON.identifier,
            Blocks.END_GATEWAY.identifier,
            Blocks.CHORUS_PLANT.identifier,
            Blocks.CHORUS_FLOWER.identifier,
            Blocks.FROSTED_ICE.identifier,
            Blocks.SEAGRASS.identifier,
            Blocks.TALL_SEAGRASS.identifier,
            Blocks.LAVA.identifier
        )

        fun relightSection(world: World, chunkPos: ChunkPos, sectionY: Int): Set<Chunk> {
            val res = relightSection(world, chunkPos, sectionY, LightType.BLOCK).toMutableList()
            res.addAll(relightSection(world, chunkPos, sectionY, LightType.SKY))
            return res.toSet()
        }

        private fun relightSection(world: World, chunkPos: ChunkPos, sectionY: Int, lightType: LightType): Set<Chunk> {
            val chunk = world.getChunk(chunkPos) ?: return setOf<Chunk>()

            synchronized(world) {
                val collected = collectRequiredNearby(world, Vector3(chunkPos.x, sectionY, chunkPos.z).toVector3d(), lightType)
                return relight(world, collected, lightType)
            }
        }

        fun relight(world: World, queue: Set<Vector3d>, type: LightType): Set<Chunk> {
            return flushQueue(world, queue, type, QueueType.INTERNAL)
        }

        fun relight(world: World, chunks: List<Chunk>): List<MutableSet<Chunk>> {
            val sections = mutableSetOf<Vector3d>()
            synchronized(world) {
                chunks.forEach { chunk ->
                    for (sectionIndex in chunk.minSection until chunk.maxSection) {
                        val section = chunk.getSection(sectionIndex)
                        section.blockLight.invalidate()
                        section.skyLight.invalidate()
                        sections.add(Vector3(chunk.chunkX, sectionIndex, chunk.chunkZ).toVector3d())
                    }
                    chunk.invalidateLightData()
                }

                val blockSections = mutableSetOf<Vector3d>()
                sections.forEach { point ->
                    blockSections.addAll(getNearbyRequired(world, point, LightType.BLOCK))
                }

                val skySections = mutableSetOf<Vector3d>()
                sections.forEach { point ->
                    skySections.addAll(getNearbyRequired(world, point, LightType.SKY))
                }

                relight(world, blockSections, LightType.BLOCK)
                relight(world, skySections, LightType.SKY)

                val chunksToRelight = mutableSetOf<Chunk>()
                blockSections.forEach { point ->
                    chunksToRelight.add(world.getChunk(point.x.toInt(), point.z.toInt()) ?: return@forEach)
                }

                skySections.forEach { point ->
                    chunksToRelight.add(world.getChunk(point.x.toInt(), point.z.toInt()) ?: return@forEach)
                }
                return listOf(chunksToRelight)
            }
        }

        private fun flushQueue(world: World, queue: Set<Vector3d>, type: LightType, queueType: QueueType): ConcurrentHashMap.KeySetView<Chunk, Boolean> {
            val sections = ConcurrentHashMap.newKeySet<Light>()
            val newQueue = ConcurrentHashMap.newKeySet<Vector3d>()

            val responseChunks = ConcurrentHashMap.newKeySet<Chunk>()
            val tasks = mutableListOf<CompletableFuture<Void>>()

            val lightLookup = LightLookup { x, y, z ->
                val chunk = world.getChunk(x, z) ?: return@LightLookup null
                if (y - chunk.minSection < 0 || y - chunk.maxSection >= 0) return@LightLookup null

                val section = chunk.getSection(y)
                return@LightLookup when(type) {
                    LightType.BLOCK -> section.blockLight
                    LightType.SKY -> section.skyLight
                }
            }

            val paletteLookup = PaletteLookup { x, y, z ->
                val chunk = world.getChunk(x, z) ?: return@PaletteLookup null
                if (y - chunk.minSection < 0 || y - chunk.maxSection >= 0) return@PaletteLookup null

                return@PaletteLookup chunk.getSection(y).blockPalette
            }

            queue.forEach { point ->
                val chunk = world.getChunk(point.x.toInt(), point.z.toInt()) ?: return@forEach
                val section = chunk.getSection(point.y.toInt())

                responseChunks.add(chunk)

                val light = when(type) {
                    LightType.BLOCK -> section.blockLight
                    LightType.SKY -> section.skyLight
                }

                val blockPalette = section.blockPalette
                val task = CompletableFuture.runAsync({
                    val toAdd = when(queueType){
                        QueueType.INTERNAL -> light.calculateInternal(
                            blockPalette,
                            ChunkPos(chunk.chunkX, chunk.chunkZ),
                            point.y.toInt(),
                            chunk.occlusionMap!!,
                            356,
                            lightLookup
                        )
                        QueueType.EXTERNAL -> light.calculateExternal(
                            blockPalette,
                            Light.getNeighbours(chunk, point.y.toInt()).toList(),
                            lightLookup,
                            paletteLookup
                        )
                    }

                    sections.add(light)

                    light.flip()
                    newQueue.addAll(toAdd)
                }, pool)
                tasks.add(task)
            }

            tasks.forEach(CompletableFuture<Void>::join)

            if(!newQueue.isEmpty()) {
                val newResponse = flushQueue(world, queue, type, queueType)
                val collection = mutableListOf<Chunk>()
                responseChunks.addAll(newResponse.toCollection(collection))
            }

            return responseChunks
        }

        private fun collectRequiredNearby(world: World, point: Vector3d, type: LightType): MutableSet<Vector3d> {
            val found = mutableSetOf<Vector3d>()
            val toCheck = java.util.ArrayDeque<Vector3d>()

            toCheck.add(point)
            found.add(point)

            while(toCheck.isNotEmpty()) {
                val current = toCheck.poll()
                val nearby = getNearbyRequired(world, current, type)
                nearby.forEach { p ->
                    if(!found.contains(p)) {
                        found.add(p)
                        toCheck.add(p)
                    }
                }
            }
            return found
        }

        private fun getNearbyRequired(world: World, point: Vector3d, type: LightType): Set<Vector3d> {
            val collected = mutableSetOf<Vector3d>()
            collected.add(point)

            var highestRegionPoint = world.dimensionType.minY - 1

            for (x in point.x.toInt() - 1..point.x.toInt() + 1) {
                for (z in point.z.toInt() - 1..point.z.toInt() + 1) {
                    val chunkCheck = world.getChunk(x, z) ?: continue

                    chunkCheck.getAndUpdateOcclusionMap()
                    highestRegionPoint = max(highestRegionPoint, chunkCheck.highestBlock)
                }
            }

            for (x in point.x.toInt() - 1..point.x.toInt() + 1) {
                for (z in point.z.toInt() - 1..point.z.toInt() + 1) {
                    val chunkCheck = world.getChunk(x, z) ?: continue

                    for(y in point.y.toInt() - 1..point.y.toInt() + 1) {
                        val sectionPosition = Vector3(x, y, z)
                        val sectionHeight: Int = world.dimensionType.minY + 16 * y
                        if ((sectionHeight + 16) > highestRegionPoint && type == LightType.SKY) continue

                        if (sectionPosition.y < chunkCheck.maxSection && sectionPosition.y >= chunkCheck.minSection) {
                            val section = chunkCheck.getSection(sectionPosition.y)
                            if(type == LightType.BLOCK && !section.blockLight.requiresUpdate()) continue
                            if(type == LightType.SKY && !section.skyLight.requiresUpdate()) continue

                            collected.add(sectionPosition.toVector3d())
                        }
                    }
                }
            }
            return collected
        }
    }

    private var occlusionMap: IntArray? = IntArray(0)

    private var partialChunkLightData: ChunkLightData? = null
    private var fullChunkLightData: ChunkLightData? = null

    private var highestBlock = 0
    private val freezeInvalidation = false

    private val packetGenerationLock = ReentrantLock()
    private val resendTimer = AtomicInteger(-1)
    private val resendDelay: Int = 100

    private var doneInit = false

    enum class LightType {
        SKY,
        BLOCK
    }

    private enum class QueueType {
        INTERNAL,
        EXTERNAL
    }

    val id: UUID = UUID.randomUUID()
    val minSection = world.dimensionType.minY / 16
    val maxSection = world.dimensionType.height / 16
    private lateinit var cachedPacket: ClientboundChunkDataPacket

    val motionBlocking: ImmutableLongArray = ImmutableLongArray(37) { 0 }
    val worldSurface: ImmutableLongArray = ImmutableLongArray(37) { 0 }

    val sections: MutableList<ChunkSection> = mutableListOf()
    val blockEntities: Int2ObjectOpenHashMap<BlockEntity> = Int2ObjectOpenHashMap(0)

    var chunkLightData: ChunkLightData = ChunkLightData()

    val packet: ClientboundChunkDataPacket
        get() {
            if (!this::cachedPacket.isInitialized) updateCache()
            return cachedPacket
        }

    fun invalidateLightData() {
        this.partialChunkLightData = null
        this.fullChunkLightData = null
    }

    fun checkSkyOcclusion(block: Block): Boolean {
        if(block == Block.AIR) return false;
        if(DIFFUSE_SKY_LIGHT.contains(block.identifier)) return true

        val shape = block.getShape()
        val occludesTop: Boolean = Block.AIR.getShape().isOccluded(shape, Direction.UP)
        val occludesBottom: Boolean = Block.AIR.getShape().isOccluded(shape, Direction.DOWN)

        return occludesBottom || occludesTop
    }

    fun invalidateNeighborsSection(coordinate: Int) {
        if(freezeInvalidation) return

        for (i in -1..1) {
            for (j in -1..1) {
                val neighboringChunk = world.getChunk(chunkX + i, chunkZ + j) ?: continue

                neighboringChunk.invalidateLightData()

                for (k in -1..1) {
                    if (k + coordinate < neighboringChunk.minSection || k + coordinate >= neighboringChunk.maxSection) continue
                    neighboringChunk.getSection(k + coordinate).blockLight.invalidate()
                    neighboringChunk.getSection(k + coordinate).skyLight.invalidate()
                }
            }
        }
    }

    fun invalidateResendDelay() {
        if (!doneInit || freezeInvalidation) {
            return
        }

        for(i in -1..1) {
            for(j in -1..1) {
                val neighborChunk = world.getChunk(chunkX + i, chunkZ + j) ?: continue
                neighborChunk.resendTimer.set(resendDelay)
            }
        }
    }

    fun updateCache() {
        val heightMap = NBT.Compound {
            it.put("MOTION_BLOCKING", NBT.LongArray(motionBlocking))
            it.put("WORLD_SURFACE", NBT.LongArray(worldSurface))
        }
        cachedPacket = ClientboundChunkDataPacket(chunkX, chunkZ, heightMap, sections, blockEntities.values, chunkLightData)
    }

    init {
        val sectionsAmount = maxSection - minSection
        repeat(sectionsAmount) {
            sections.add(ChunkSection.empty())
        }
        doneInit = true
        updateLight()
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

        this.occlusionMap = null

        val coordinate = ChunkUtils.globalToChunk(y)
        if(doneInit && !freezeInvalidation) {
            invalidateNeighborsSection(coordinate)
            invalidateResendDelay()
        }

        fun sendLighting() {
            //TODO Light packet
//            world.players.sendPacket()
        }

    }


    fun updateLight() {
        for (section in minSection until maxSection) {
            getSection(section).blockLight.invalidate()
            getSection(section).skyLight.invalidate()
        }

        invalidateLightData()
        for(i in -1..1) {
            for(j in -1..1) {
                val neighborChunk = world.getChunk(chunkX + i, chunkZ + j) ?: continue

                if(neighborChunk.doneInit) {
                    neighborChunk.resendTimer.set(20)
                    neighborChunk.invalidateLightData()

                    for (section in minSection until maxSection) {
                        neighborChunk.getSection(section).blockLight.invalidate()
                        neighborChunk.getSection(section).skyLight.invalidate()
                    }
                }
            }
        }
    }

    fun getAndUpdateOcclusionMap(): IntArray? {
        if(this.occlusionMap != null) return this.occlusionMap
        val occlusionMap = IntArray(16 * 16)

        val minY = world.dimensionType.minY
        highestBlock = minY - 1

        synchronized(this) {
            val startY = ChunkUtils.getHighestBlockSection(this)

            for (x in 0 until 16) {
                for (z in 0 until 16) {
                    var height = startY
                    while(height >= minY) {
                        val block = getBlock(x, height, z)
                        if(block != Block.AIR) highestBlock = max(highestBlock, height)
                        if(checkSkyOcclusion(block)) break
                        height--
                    }
                    occlusionMap[z shl 4 or x] = (height + 1)
                }
            }
        }

        this.occlusionMap = occlusionMap
        return occlusionMap
    }

    fun createLightData(requiredFullChunk: Boolean): ChunkLightData {
        packetGenerationLock.lock()
        if(requiredFullChunk) {
            if(fullChunkLightData != null) {
                packetGenerationLock.unlock()
                return fullChunkLightData!!
            }
        } else {
            if(partialChunkLightData != null) {
                packetGenerationLock.unlock()
                return partialChunkLightData!!
            }
        }

        val skyMask = BitSet()
        val blockMask = BitSet()
        val emptySkyMask = BitSet()
        val emptyBlockMask = BitSet()

        val skyLights: MutableList<ByteArray> = mutableListOf()
        val blockLights: MutableList<ByteArray> = mutableListOf()

        val chunkMin = world.dimensionType.minY
        var highestNeighborBlock = world.dimensionType.minY

        for(i in -1..1) {
            for(j in -1..1) {
                val neighbor = world.getChunk(chunkX + i, chunkZ + j) ?: continue
                neighbor.getAndUpdateOcclusionMap()
                highestNeighborBlock = max(highestNeighborBlock, neighbor.highestBlock)
            }
        }

        var index = 0
        sections.forEach { section ->
            var wasUpdatedBlock = false
            var wasUpdatedSky = false

            if(section.skyLight.requiresUpdate()) {
                //TODO relightSection
                wasUpdatedSky = true
            } else if(requiredFullChunk || section.blockLight.requiresSend) {
                wasUpdatedSky = true
            }

            if(section.blockLight.requiresUpdate()) {
                //TODO relightSection
                wasUpdatedBlock = true
            } else if(requiredFullChunk || section.skyLight.requiresSend) {
                wasUpdatedBlock = true
            }

            val sectionMinY = index * 16 + chunkMin
            index++

            if((wasUpdatedSky && this.world.dimensionType.hasSkylight && sectionMinY <= (highestNeighborBlock + 16))) {
                val skyLight = section.skyLight.byteArray

                if(skyLight.isNotEmpty() && !skyLight.contentEquals(LightEngine.EMPTY_CONTENT)) {
                    skyLights.add(skyLight)
                    skyMask.set(index)
                } else {
                    emptySkyMask.set(index)
                }
            }

            if(wasUpdatedBlock) {
                val blockLight = section.blockLight.byteArray

                if(blockLight.isNotEmpty() && !blockLight.contentEquals(LightEngine.EMPTY_CONTENT)) {
                    blockLights.add(blockLight)
                    blockMask.set(index)
                } else {
                    emptyBlockMask.set(index)
                }
            }
        }

        val chunkLightData = ChunkLightData(skyMask, blockMask, emptySkyMask, emptyBlockMask, skyLights, blockLights)
        if(requiredFullChunk) {
            this.fullChunkLightData = chunkLightData
        } else {
            partialChunkLightData = chunkLightData
        }

        packetGenerationLock.unlock()

        return chunkLightData
    }

    fun getBlock(x: Int, y: Int, z: Int): Block {
        val customDataBlock = world.customDataBlocks[Location(x, y, z, world).blockHash]
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