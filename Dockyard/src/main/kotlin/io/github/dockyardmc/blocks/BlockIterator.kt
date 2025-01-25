package io.github.dockyardmc.blocks

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.Vector3d
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

class BlockIterator(var start: Location, direction: Vector3d, yOffset: Double, maxDistance: Double, var smooth: Boolean) : Iterator<Vector3> {

    constructor(entity: Entity, maxDistance: Int): this(entity.location, entity.type.dimensions.eyeHeight.toDouble(), maxDistance)

    constructor(location: Location, yOffset: Double, maxDistance: Int): this(location, location.getDirection(), yOffset, maxDistance.toDouble(), false)

    private val signums = ShortArray(3)

    var mapX = start.blockX
    var mapY = start.blockY
    var mapZ = start.blockZ

    private var deltaDistX = 0.0
    private var deltaDistY = 0.0
    private var deltaDistZ = 0.0

    var sideDistX: Double = 0.0
    var sideDistY: Double = 0.0
    var sideDistZ: Double = 0.0

    var foundEnd = false
    val extraPoints: ArrayDeque<Vector3> = ArrayDeque<Vector3>()
    private var end: Location? = null

    init {
        reset(start, direction, yOffset, maxDistance, smooth)
    }

    fun reset(start: Location, direction: Vector3d, yOffset: Double, maxDistance: Double, smooth: Boolean) {
        extraPoints.clear()
        foundEnd = false

        this.start = start.add(0.0, yOffset, 0.0)

        end = if(maxDistance != 0.0) start.add(direction.normalized() * Vector3d(maxDistance)) else null
        if (direction.isZero) this.foundEnd = true

        val ray: Vector3d = direction.normalized()
        mapX = start.blockX
        mapY = start.blockY
        mapZ = start.blockZ

        signums[0] = sign(direction.x).toInt().toShort()
        signums[1] = sign(direction.y).toInt().toShort()
        signums[2] = sign(direction.z).toInt().toShort()

        deltaDistX = if ((ray.x == 0.0)) 1e30 else abs(1.0 / ray.x)
        deltaDistY = if ((ray.y == 0.0)) 1e30 else abs(1.0 / ray.y) // Find grid intersections for x, y, z
        deltaDistZ = if ((ray.z == 0.0)) 1e30 else abs(1.0 / ray.z) // This works by calculating and storing the distance to the next grid intersection on the x, y and z axis

        //calculate step and initial sideDist
        sideDistX = if (ray.x < 0) (start.x - mapX) * deltaDistX
        else if (ray.x > 0) (mapX + signums[0] - start.x) * deltaDistX
        else Double.MAX_VALUE
        
        sideDistY = if (ray.y < 0) (start.y - mapY) * deltaDistY
        else if (ray.y > 0) (mapY + signums[1] - start.y) * deltaDistY
        else Double.MAX_VALUE

        sideDistZ = if (ray.z < 0) (start.z - mapZ) * deltaDistZ
        else if (ray.z > 0) (mapZ + signums[2] - start.z) * deltaDistZ
        else Double.MAX_VALUE
    }

    override fun hasNext(): Boolean {
        return !foundEnd
    }

    override fun next(): Vector3 {
        if (foundEnd) throw NoSuchElementException()
        if (!extraPoints.isEmpty()) {
            val res: Vector3 = extraPoints.poll()
            if (end != null && res.equalsBlock(end!!)) foundEnd = true
            return res
        }

        val current = Vector3(mapX, mapY, mapZ)
        if (end != null && end!!.sameBlock(current)) foundEnd = true

        val closest: Double = min(sideDistX, min(sideDistY, sideDistZ))
        val needsX = sideDistX - closest < 1e-10 && signums[0].toInt() != 0
        val needsY = sideDistY - closest < 1e-10 && signums[1].toInt() != 0
        val needsZ = sideDistZ - closest < 1e-10 && signums[2].toInt() != 0

        if (needsZ) {
            sideDistZ += deltaDistZ
            mapZ += signums[2].toInt()
        }

        if (needsX) {
            sideDistX += deltaDistX
            mapX += signums[0].toInt()
        }

        if (needsY) {
            sideDistY += deltaDistY
            mapY += signums[1].toInt()
        }

        if (needsX && needsY && needsZ) {
            extraPoints.add(Vector3(signums[0] + current.x, signums[1] + current.y, current.z))
            if (smooth) return current

            extraPoints.add(Vector3(current.x, signums[1] + current.y, signums[2] + current.z))
            extraPoints.add(Vector3(signums[0] + current.x, current.y, signums[2] + current.z))

            extraPoints.add(Vector3(signums[0] + current.x, current.y, current.z))
            extraPoints.add(Vector3(current.x, signums[1] + current.y, current.z))
            extraPoints.add(Vector3(current.x, current.y, signums[2] + current.z))
        } else if (needsX && needsY) {
            extraPoints.add(Vector3(signums[0] + current.x, current.y, current.z))
            if (smooth) return current
            extraPoints.add(Vector3(current.x, signums[1] + current.y, current.z))
        } else if (needsX && needsZ) {
            extraPoints.add(Vector3(signums[0] + current.x, current.y, current.z))
            if (smooth) return current
            extraPoints.add(Vector3(current.x, current.y, signums[2] + current.z))
        } else if (needsY && needsZ) {
            extraPoints.add(Vector3(current.x, signums[1] + current.y, current.z))
            if (smooth) return current
            extraPoints.add(Vector3(current.x, current.y, signums[2] + current.z))
        }
        return current
    }
}