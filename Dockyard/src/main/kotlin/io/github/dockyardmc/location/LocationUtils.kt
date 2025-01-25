package io.github.dockyardmc.location

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max

object LocationUtils {

    fun getRotationYaw(dx: Double, dz: Double): Float {
        val radians = atan2(dz, dx)
        val degrees = Math.toDegrees(radians).toFloat() - 90
        if (degrees < -180) return degrees + 360
        if (degrees > 180) return degrees - 360
        return degrees
    }

    fun getRotationPitch(dx: Double, dy: Double, dz: Double): Float {
        val radians = -atan2(dy, max(abs(dx), abs(dz)))
        return Math.toDegrees(radians).toFloat()
    }

    fun fixedYaw(yaw: Float): Float {
        var newYaw: Float = yaw % 360
        if (newYaw < -180.0f) {
            newYaw += 360.0f
        } else if (newYaw > 180.0f) {
            newYaw -= 360.0f
        }
        return newYaw
    }
}