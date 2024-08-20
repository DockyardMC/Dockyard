package io.github.dockyardmc.entities

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.utils.toVector3
import io.github.dockyardmc.utils.toVector3f
import io.github.dockyardmc.world.World
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sqrt

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

class KinematicChain(location: Location, world: World, segmentAmounts: Int) {

    val segments: MutableList<BlockDisplay> = mutableListOf()

    init {
        for (i in 0 until segmentAmounts) {
            val sizeBefore = (0.3f - ((i - 1f) / 10f)) * 0.5
            val size = 0.3f - (i / 10f)
            val display = BlockDisplay(location.withNoRotation().add(0.0, i.toDouble(), 0.0).subtract(sizeBefore, 0.0, sizeBefore), world)
            display.block.value = Blocks.NETHERITE_BLOCK
            display.scaleTo(size, 1f, size)
            display.interpolationDelay.value = 0
            display.transformInterpolation.value = 10
            display.translationInterpolation.value = 10
            segments.add(display)
            world.spawnEntity(display)
        }
    }

    fun setLocation(location: Location) {
        val last = segments.last()
        val direction = location.subtract(last.location).toVector3f().normalize()
        DockyardServer.broadcastMessage("$direction")

        val yaw = atan2(direction.z.toDouble(), direction.x.toDouble()).toFloat()

        val horizontalDistance = sqrt((direction.x * direction.x + direction.z * direction.z).toDouble())
        val pitch = atan2(direction.y.toDouble(), horizontalDistance).toFloat()

        val yawDegrees = Math.toDegrees(yaw.toDouble()).toFloat()
        val pitchDegrees = Math.toDegrees(pitch.toDouble()).toFloat()

        val yawAdjusted = (yawDegrees + 90) % 360
        val pitchAdjusted = (pitchDegrees + 90) % 360

        DockyardServer.broadcastMessage("<yellow>$yawAdjusted <gray>| <yellow>$pitchAdjusted <gray>| <yellow>0f")
        last.rotation.value = Vector3f(pitchAdjusted, yawAdjusted, 0f)
    }
}