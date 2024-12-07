package io.github.dockyardmc.shapes

import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.utils.vectors.Vector3d
import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min

data class Shape(
    val collisionData: CollisionData,
    val lightData: LightData
) {

    companion object {
        val PATTERN: Pattern = Pattern.compile("\\d.\\d+", Pattern.MULTILINE)

        fun isFaceCovered(covering: List<Rectangle>): FaceCoverage {
            if (covering.isEmpty()) return FaceCoverage.NOT_COVERED

            val r = Rectangle(0, 0, 1, 1)
            var toCover = mutableListOf<Rectangle>(r)

            covering.forEach { rect ->
                val nextCovering = mutableListOf<Rectangle>()
                toCover.forEach { toCoverRect ->
                    val remaining = getRemaining(rect, toCoverRect)
                    nextCovering.addAll(remaining)
                }
                toCover = nextCovering
                if (toCover.isEmpty()) return FaceCoverage.FULLY
            }
            return FaceCoverage.PARTIALLY
        }

        private fun getRemaining(
            covering: Rectangle,
            toCover: Rectangle
        ): List<Rectangle> {
            var covering1: Rectangle = covering
            val remaining: MutableList<Rectangle> = mutableListOf<Rectangle>()
            covering1 = clipRectangle(covering1, toCover)
            // Up
            if (covering1.minY > toCover.minY) {
                remaining.add(Rectangle(toCover.minX, toCover.minY, toCover.maxX, covering1.minY))
            }
            // Down
            if (covering1.maxY < toCover.maxY) {
                remaining.add(Rectangle(toCover.minX, covering1.maxY, toCover.maxX, toCover.maxY))
            }
            // Left
            if (covering1.minX > toCover.minX) {
                remaining.add(Rectangle(toCover.minX, covering1.minY, covering1.minX, covering1.maxY))
            }
            //Right
            if (covering1.maxX < toCover.maxX) {
                remaining.add(Rectangle(covering1.maxX, covering1.minY, toCover.maxX, covering1.maxY))
            }
            return remaining
        }

        fun clipRectangle(covering: Rectangle, toCover: Rectangle): Rectangle {
            val minX = max(covering.minX, toCover.minX)
            val minY = max(covering.minY, toCover.minY)

            val maxX = min(covering.maxX, toCover.maxX)
            val maxY = min(covering.maxY, toCover.maxY)

            return Rectangle(minX, minY, maxX, maxY)
        }

        fun computeOcclusionSet(face: Direction, boundingBoxes: List<BoundingBox>): MutableList<Rectangle> {
            val rSet = mutableListOf<Rectangle>()
            boundingBoxes.forEach { boundingBox ->
                when (face) {
                    Direction.NORTH -> if (boundingBox.minZ == 0.0) rSet.add(
                        Rectangle(
                            boundingBox.minZ,
                            boundingBox.minY,
                            boundingBox.maxX,
                            boundingBox.maxY
                        )
                    )

                    Direction.SOUTH -> if (boundingBox.maxZ == 1.0) rSet.add(
                        Rectangle(
                            boundingBox.minX,
                            boundingBox.minY,
                            boundingBox.maxX,
                            boundingBox.maxY
                        )
                    )

                    Direction.WEST -> if (boundingBox.minX == 0.0) rSet.add(
                        Rectangle(
                            boundingBox.minX,
                            boundingBox.minY,
                            boundingBox.maxX,
                            boundingBox.maxY
                        )
                    )

                    Direction.EAST -> if (boundingBox.maxX == 1.0) rSet.add(
                        Rectangle(
                            boundingBox.minY,
                            boundingBox.minZ,
                            boundingBox.maxY,
                            boundingBox.maxZ
                        )
                    )

                    Direction.DOWN -> if (boundingBox.minY == 0.0) rSet.add(
                        Rectangle(
                            boundingBox.minX,
                            boundingBox.minZ,
                            boundingBox.maxX,
                            boundingBox.maxZ
                        )
                    )

                    Direction.UP -> if (boundingBox.maxY == 1.0) rSet.add(
                        Rectangle(
                            boundingBox.minX,
                            boundingBox.minZ,
                            boundingBox.maxX,
                            boundingBox.maxZ
                        )
                    )
                }
            }
            return rSet
        }

        fun parseBlockFromRegistry(collision: String, occlusion: String, occludes: Boolean, lightEmission: Int): Shape {
            val collisionBoundingBoxes = parseRegistryBoundingBoxString(collision)
            val occlusionBoundingBoxes = if(occludes) parseRegistryBoundingBoxString(occlusion) else listOf()
            val collisionData = collisionData(collisionBoundingBoxes)
            val lightData = lightData(occlusionBoundingBoxes, lightEmission)
            val shape = Shape(collisionData, lightData)
            return shape
        }

        fun parseRegistryBoundingBoxString(string: String): List<BoundingBox> {
            val matcher = PATTERN.matcher(string)
            val vals = DoubleArrayList()
            while(matcher.find()) {
                val newVal = matcher.group().toDouble()
                vals.add(newVal)
            }
            val count: Int = vals.size / 6
            val boundingBoxes = arrayOfNulls<BoundingBox>(count)
            for (i in 0 until count) {
                val minX: Double = vals.getDouble(0 + 6 * i)
                val minY: Double = vals.getDouble(1 + 6 * i)
                val minZ: Double = vals.getDouble(2 + 6 * i)

                val boundXSize: Double = vals.getDouble(3 + 6 * i) - minX
                val boundYSize: Double = vals.getDouble(4 + 6 * i) - minY
                val boundZSize: Double = vals.getDouble(5 + 6 * i) - minZ

                val min = Vector3d(minX, minY, minZ)
                val max = Vector3d(minX + boundXSize, minY + boundYSize, minZ + boundZSize)

                val boundingBox = BoundingBox(min, max)
                assert(boundingBox.minX == minX)
                assert(boundingBox.minY == minY)
                assert(boundingBox.minZ == minZ)
                boundingBoxes[i] = boundingBox
            }
            return boundingBoxes.toList().requireNoNulls()
        }

        fun collisionData(collisionBoundingBoxes: List<BoundingBox>): CollisionData {
            val relativeStart: Vector3d
            val relativeEnd: Vector3d

            if(collisionBoundingBoxes.isEmpty()) {
                relativeStart = Vector3d(0.0)
                relativeEnd = Vector3d(0.0)
            } else {
                var minX = 1.0
                var minY = 1.0
                var minZ = 1.0
                var maxX = 0.0
                var maxY = 0.0
                var maxZ = 0.0

                collisionBoundingBoxes.forEach { blockSection ->

                    // Min
                    if (blockSection.minX < minX) minX = blockSection.minX
                    if (blockSection.minY < minY) minY = blockSection.minY
                    if (blockSection.minZ < minZ) minZ = blockSection.minZ

                    // Max
                    if (blockSection.maxX > maxX) maxX = blockSection.maxX
                    if (blockSection.maxY > maxY) maxY = blockSection.maxY
                    if (blockSection.maxZ > maxZ) maxZ = blockSection.maxZ
                }
                relativeStart = Vector3d(minX, minY, minZ)
                relativeEnd = Vector3d(maxX, maxY, maxZ)
            }
            var fullCollisionFaces: Byte = 0
            Direction.entries.forEach { face ->
                val res = isFaceCovered(computeOcclusionSet(face, collisionBoundingBoxes)).ordinal.toByte()
                fullCollisionFaces = (fullCollisionFaces.toInt() or ((if ((res.toInt() == 2)) 1 else 0) shl (face.ordinal.toByte()).toInt())).toByte()
            }
            return CollisionData(collisionBoundingBoxes, relativeStart, relativeEnd, fullCollisionFaces.toInt())
        }

        fun lightData(occlusionBoundingBoxes: List<BoundingBox>, lightEmission: Int): LightData {
            var fullFaces: Byte = 0
            var airFaces: Byte = 0
            Direction.entries.forEach { face ->
                val res = isFaceCovered(computeOcclusionSet(face, occlusionBoundingBoxes)).ordinal.toByte()
                fullFaces = (fullFaces.toInt() or ((if ((res.toInt() == 2)) 1 else 0) shl (face.ordinal.toByte()).toInt())).toByte()
                airFaces = (airFaces.toInt() or ((if ((res.toInt() == 0)) 1 else 0) shl (face.ordinal.toByte()).toInt())).toByte()
            }

            return LightData(occlusionBoundingBoxes, fullFaces.toInt(), airFaces.toInt(), lightEmission)
        }
    }

    fun isOccluded(shape: Shape, face: Direction): Boolean {
        val lightData = this.lightData
        val otherLightData: LightData = shape.lightData

        val hasBlockOcclusion = (((lightData.blockOcclusion shr face.ordinal) and 1) == 1)
        val hasBlockOcclusionOther = ((otherLightData.blockOcclusion shr face.getOppositeFace().ordinal) and 1) == 1

        if (lightData.lightEmission > 0) return hasBlockOcclusionOther

        if (hasBlockOcclusion || hasBlockOcclusionOther) return true

        val hasAirOcclusion = (((lightData.airOcclusion shr face.ordinal) and 1) == 1)
        val hasAirOcclusionOther = ((otherLightData.airOcclusion shr face.getOppositeFace().ordinal) and 1) == 1

        if (hasAirOcclusion || hasAirOcclusionOther) return false

        val allRectangles: MutableList<Rectangle> = computeOcclusionSet(face.getOppositeFace(), otherLightData.occlusionBoundingBoxes)
        allRectangles.addAll(computeOcclusionSet(face, lightData.occlusionBoundingBoxes))

        return isFaceCovered(allRectangles) == FaceCoverage.FULLY
    }

    enum class FaceCoverage {
        NOT_COVERED,
        PARTIALLY,
        FULLY
    }

    data class CollisionData(
        val collisionBoundingBoxes: List<BoundingBox>,
        val relativeStart: Vector3d,
        val relativeEnd: Vector3d,
        val fullFaces: Int
    )

    data class LightData(
        val occlusionBoundingBoxes: List<BoundingBox>,
        val blockOcclusion: Int,
        val airOcclusion: Int,
        val lightEmission: Int
    )

}

