package io.github.dockyardmc.world.light

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.world.palette.Palette
import it.unimi.dsi.fastutil.shorts.ShortArrayFIFOQueue
import java.util.*
import kotlin.math.max

object LightEngine {
    val DIRECTIONS: List<Direction> = Direction.entries.toList()
    const val LIGHT_LENGTH: Int = 16 * 16 * 16 / 2
    const val SECTION_SIZE: Int = 16

    val EMPTY_CONTENT: ByteArray = ByteArray(LIGHT_LENGTH)
    val CONTENT_FULLY_LIT: ByteArray = ByteArray(LIGHT_LENGTH)

    init {
        Arrays.fill(CONTENT_FULLY_LIT, (-1).toByte())
    }

    fun compute(blockPalette: Palette, lightPre: ShortArrayFIFOQueue): ByteArray {
        if (lightPre.isEmpty) return EMPTY_CONTENT

        val lightArray = ByteArray(LIGHT_LENGTH)
        val lightSources = ShortArrayFIFOQueue()

        while (!lightPre.isEmpty) {
            val index = lightSources.dequeueShort().toInt()
            val x: Int = index and 15
            val z: Int = (index shr 4) and 15
            val y: Int = (index shr 8) and 15
            val lightLevel: Int = (index shr 12) and 15
            val newLightLevel = lightLevel - 1

            DIRECTIONS.forEach { direction ->
                val xO: Int = x + direction.normalX
                val yO: Int = y + direction.normalY
                val zO: Int = z + direction.normalZ

                // le border
                if (xO < 0 || xO >= SECTION_SIZE || yO < 0 || yO >= SECTION_SIZE || zO < 0 || zO >= SECTION_SIZE) {
                    return@forEach
                }

                // section index
                val newIndex = xO or (zO shl 4) or (yO shl 8)

                if (getLight(lightArray, newIndex) < newLightLevel) {
                    val currentBlock = Objects.requireNonNullElse(getBlock(blockPalette, x, y, z), Block.AIR)
                    val propagatedBlock = Objects.requireNonNullElse(getBlock(blockPalette, xO, yO, zO), Block.AIR)

                    val airAir = currentBlock.isAir() && propagatedBlock.isAir()

                    //TODO shape registry adn occlusion

                    placeLight(lightArray, newIndex, newLightLevel)
                    lightSources.enqueue((newIndex or (newLightLevel.toInt() shl 12)).toShort())
                }
            }
        }

        return lightArray
    }

    fun getLight(light: ByteArray, index: Int): Int {
        if (index ushr 1 >= light.size) return 0
        val value = light[index ushr 1].toInt()
        return ((value ushr ((index and 1) shl 2)) and 0xF)
    }

    fun getLight(light: ByteArray, x: Int, y: Int, z: Int): Int {
        return getLight(light, x or (z shl 4) or (y shl 8))
    }

    fun getBlock(palette: Palette, x: Int, y: Int, z: Int): Block {
        return Block.getBlockByStateId(palette[x, y, z])
    }

    private fun placeLight(light: ByteArray, index: Int, value: Int) {
        val shift = (index and 1) shl 2
        val i = index ushr 1
        light[i] = ((light[i].toInt() and (0xF0 ushr shift)) or (value shl shift)).toByte()
    }

    fun bake(content1: ByteArray?, content2: ByteArray?): ByteArray {
        if (content1 == null && content2 == null) return EMPTY_CONTENT
        if (content1.contentEquals(EMPTY_CONTENT) && content2.contentEquals(EMPTY_CONTENT)) return EMPTY_CONTENT

        if (content1 == null) return content2!!
        if (content2 == null) return content1

        if (content1.contentEquals(EMPTY_CONTENT) && content2.contentEquals(EMPTY_CONTENT)) return EMPTY_CONTENT

        val lightMax = ByteArray(LIGHT_LENGTH)
        for (i in content1.indices) {
            val c1 = content1[i]
            val c2 = content2[i]

            // Lower
            val l1 = (c1.toInt() and 0x0F).toByte()
            val l2 = (c2.toInt() and 0x0F).toByte()

            // Upper
            val u1 = ((c1.toInt() shr 4) and 0x0F).toByte()
            val u2 = ((c2.toInt() shr 4) and 0x0F).toByte()

            val lower = max(l1.toDouble(), l2.toDouble())
            val upper = max(u1.toDouble(), u2.toDouble())

            lightMax[i] = (lower.toInt() or (upper.toInt() shl 4)).toByte()
        }
        return lightMax
    }

    fun compareBorders(
        content: ByteArray?,
        contentPropagation: ByteArray?,
        contentPropagationTemp: ByteArray?,
        face: Direction
    ): Boolean {
        if (content == null && contentPropagation == null && contentPropagationTemp == null) return true

        val k = when (face) {
            Direction.WEST, Direction.DOWN, Direction.NORTH -> 0
            Direction.EAST, Direction.UP, Direction.SOUTH -> 15
        }

        for (bx in 0 until SECTION_SIZE) {
            for (by in 0 until SECTION_SIZE) {
                val posFrom = when (face) {
                    Direction.NORTH, Direction.SOUTH -> bx or (k shl 4) or (by shl 8)
                    Direction.WEST, Direction.EAST -> k or (by shl 4) or (bx shl 8)
                    else -> bx or (by shl 4) or (k shl 8)
                }

                val valueFrom: Int = if (content == null && contentPropagation == null) 0
                else if (content != null && contentPropagation == null) getLight(content, posFrom)
                else if (content == null) getLight(contentPropagation!!, posFrom)
                else max(
                    getLight(content, posFrom).toDouble(),
                    getLight(contentPropagation!!, posFrom).toDouble()
                ).toInt()

                val valueTo: Int = getLight(contentPropagationTemp!!, posFrom)
                if (valueFrom < valueTo) return false
            }
        }
        return true
    }
}