package io.github.dockyardmc.entities

import io.github.dockyardmc.entities.EntityManager.despawnEntity
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.utils.toVector3f
import io.github.dockyardmc.world.World

class Mimic(location: Location, world: World) {
    val legs: MutableMap<Int, KinematicChain> = mutableMapOf()
    val segments: Int = 3

    init {
        legs[1] = KinematicChain(location, world, segments)
        legs[2] = KinematicChain(location, world, segments)
        legs[3] = KinematicChain(location, world, segments)
        legs[4] = KinematicChain(location, world, segments)
    }
}

class KinematicChain(val root: Location, val world: World, val segmentAmounts: Int, val segmentScale: Float = 1f) {

    val segments: MutableList<BlockDisplay> = mutableListOf()

    init {
        for (i in 0 until segmentAmounts) {
            val sizeBefore = (0.3f - ((i - 1f) / 10f)) * 0.5
            val size = 0.3f - (i / 10f)
            val display = BlockDisplay(root.withNoRotation().add(0.0, i.toDouble(), 0.0).subtract(sizeBefore, 0.0, sizeBefore), world)
            display.block.value = Blocks.AIR
            display.scaleTo(size, segmentScale, size)
            display.interpolationDelay.value = 0
            display.transformInterpolation.value = 10
            display.translationInterpolation.value = 10
            segments.add(display)
            world.spawnEntity(display)
        }
    }

    private var isDisposing = false
    fun dispose() {
        isDisposing = true
        segments.forEach {
            world.despawnEntity(it)
        }
    }

    fun fabrik(target: Location) {
        val tolerance = 0.01

        for (i in 0 until 10) {
            if(isDisposing) break
            fabrikForward(target)
            fabrikBackward()

            if(getEndEffector().distance(target.toVector3f()) < tolerance) {
                break
            }
        }
    }

    fun fabrikForward(newPosition: Location) {
        val lastSegment = segments.last()
        lastSegment.location = newPosition

        for(i in segments.size -1 downTo 1) {
            val previousSegment = segments[i]
            val segment = segments[i - 1]

            moveSegment(segment, previousSegment.location, previousSegment.scale.value.y.toDouble())
        }
    }


    fun fabrikBackward() {
        moveSegment(segments.first(), root, segments.first().scale.value.y.toDouble())

        for (i in 1 until segments.size) {
            val previousSegment = segments[i - 1]
            val segment = segments[i]

            moveSegment(segment, previousSegment.location, segment.scale.value.y.toDouble())
        }
    }


    fun straightenDirection(direction: Vector3f) {
        direction.normalize()
        val position = root.clone()
        for (segment in segments) {
            position.add(direction.multiply(segment.scale.value.y.toDouble()))
            segment.location = position
        }
    }

    fun moveSegment(segment: BlockDisplay, pullTowards: Location, segmentLen: Double) {
        val point = segment.location
        val direction = pullTowards.subtract(point).toVector3f().normalize()

        segment.location = pullTowards.subtract(direction.multiply(segmentLen))
    }

    fun getEndEffector(): Vector3f = segments.last().location.toVector3f()
}


