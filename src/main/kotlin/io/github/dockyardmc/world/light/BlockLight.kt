package io.github.dockyardmc.world.light

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.world.chunk.ChunkPos
import io.github.dockyardmc.world.light.LightingEngine.EMPTY_CONTENT
import io.github.dockyardmc.world.light.LightingEngine.getBlock
import io.github.dockyardmc.world.light.LightingEngine.getLight
import io.github.dockyardmc.world.palette.Palette
import it.unimi.dsi.fastutil.shorts.ShortArrayFIFOQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max

class BlockLight : Light {

    private var content: ByteArray? = null
    private var contentPropagation: ByteArray? = null
    private var contentPropagationSwap: ByteArray? = null

    private var isValidBorders = true
    private val needsSend = AtomicBoolean(false)

    override fun flip() {
        if (this.contentPropagationSwap != null) {
            this.contentPropagation = this.contentPropagationSwap
        }
        this.contentPropagationSwap = null
    }

    companion object {
        fun buildInternalQueue(blockPalette: Palette): ShortArrayFIFOQueue {
            val lightSources = ShortArrayFIFOQueue()

            blockPalette.getAllPresent { x, y, z, stateId ->
                val block = Block.getBlockByStateId(stateId)
                val lightEmission = block.registryBlock.lightEmission

                val index = x or (z shl 4) or (y shl 8)
                if (lightEmission > 0) {
                    lightSources.enqueue((index or (lightEmission shl 12)).toShort())
                }
            }

            return lightSources
        }

    }

    fun buildExternalQueue(
        blockPalette: Palette,
        neighbors: List<Vector3?>,
        content: ByteArray?,
        lightLookup: LightLookup,
        paletteLookup: PaletteLookup
    ): ShortArrayFIFOQueue {
        val lightSources = ShortArrayFIFOQueue()
        for (i in neighbors.indices) {
            val face = Direction.entries[i]
            val neighborSection = neighbors[i] ?: continue

            val otherPalette = paletteLookup.palette(neighborSection.x, neighborSection.y, neighborSection.z)
            val otherLight = lightLookup.light(neighborSection.x, neighborSection.y, neighborSection.z)

            for (bx in 0 until 16) {
                for (by in 0 until 16) {
                    val k = when (face) {
                        Direction.WEST,
                        Direction.DOWN,
                        Direction.NORTH -> 0

                        else -> 15
                    }

                    val lightEmissionBase = when (face) {
                        Direction.NORTH,
                        Direction.SOUTH -> otherLight.getLevel(bx, by, 15)

                        Direction.WEST,
                        Direction.EAST -> otherLight.getLevel(15 - k, bx, by)

                        else -> otherLight.getLevel(bx, 15 - k, by)
                    }
                    val lightEmission = max(lightEmissionBase - 1, 0)

                    val posTo = when (face) {
                        Direction.NORTH,
                        Direction.SOUTH -> bx or (k shl 4) or (by shl 8)

                        Direction.WEST,
                        Direction.EAST -> k or (by shl 4) or (bx shl 8)

                        else -> bx or (by shl 4) or (k shl 8)
                    }

                    if (content != null) {
                        val internalEmission = (max(getLight(content, posTo) - 1, 0))
                        if (lightEmission <= internalEmission) continue
                    }

                    val blockTo: Block = when (face) {
                        Direction.NORTH,
                        Direction.SOUTH -> getBlock(blockPalette, bx, by, k)

                        Direction.WEST,
                        Direction.EAST -> getBlock(blockPalette, k, bx, by)

                        else -> getBlock(blockPalette, bx, k, by)
                    }

                    val blockFrom = (when (face) {
                        Direction.NORTH,
                        Direction.SOUTH -> getBlock(otherPalette, bx, by, 15 - k)

                        Direction.WEST,
                        Direction.EAST -> getBlock(otherPalette, 15 - k, bx, by)

                        else -> getBlock(otherPalette, bx, 15 - k, by)
                    })

                    //TODO Shape registry & occlusion

                    if (lightEmission > 0) {
                        val index = posTo or (lightEmission shl 12)
                        lightSources.enqueue(index.toShort())
                    }
                }
            }
        }
        return lightSources
    }

    override fun getLevel(x: Int, y: Int, z: Int): Int {
        if (content == null) return 0
        val index = x or (z shl 4) or (y shl 8)
        if (contentPropagation == null) return getLight(content!!, index)
        return max(getLight(contentPropagation!!, index), getLight(content!!, index))

    }

    override fun invalidate() {
        this.needsSend.set(true)
        this.isValidBorders = false
        this.contentPropagation = null
    }

    override fun requiresUpdate(): Boolean {
        return !isValidBorders
    }

    override fun set(copyArray: ByteArray) {
        this.content = copyArray.clone()
        this.contentPropagation = this.content
        this.isValidBorders = true
        this.needsSend.set(true)
    }

    override fun calculateInternal(
        palette: Palette,
        chunkPos: ChunkPos,
        heightmap: IntArray,
        maxY: Int,
        lookup: LightLookup
    ): Set<Vector3> {
        TODO("Not yet implemented")
    }

    override fun calculateExternal(
        palette: Palette,
        neighbours: List<Vector3>,
        lightLookup: LightLookup,
        paletteLookup: PaletteLookup
    ): Set<Vector3> {
        TODO("Not yet implemented")
    }

    override val requiresSend: Boolean get() = needsSend.getAndSet(false)
    override val byteArray: ByteArray get() {
        if(content == null) ByteArray(0)
        if(contentPropagation == null) return content!!
        val res = LightingEngine.bake(contentPropagation, content)
        if(res.contentEquals(EMPTY_CONTENT)) return ByteArray(0)
        return res
    }
}