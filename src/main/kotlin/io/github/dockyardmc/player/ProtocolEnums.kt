package io.github.dockyardmc.player

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.utils.vectors.Vector3d
import io.github.dockyardmc.utils.vectors.Vector3f
import kotlin.experimental.or
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class PlayerHand {
    MAIN_HAND,
    OFF_HAND
}

enum class ClientParticleSettings {
    ALL,
    DECREASED,
    MINIMAL
}

enum class PlayerAction {
    SNEAKING_START,
    SNEAKING_STOP,
    LEAVE_BED,
    SPRINTING_START,
    SPRINTING_END,
    HORSE_JUMP_START,
    HORSE_JUMP_END,
    VEHICLE_INVENTORY_OPEN,
    ELYTRA_FLYING_START
}

enum class EntityPose {
    STANDING,
    FALL_FLYING,
    SLEEPING,
    SWIMMING,
    SPIN_ATTACK,
    SNEAKING,
    LONG_JUMPING,
    DYING,
    CROAKING,
    USING_TONGUE,
    SITTING,
    ROARING,
    SNIFFING,
    EMERGING,
    DIGGING,
    SLIDING,
    SHOOTING,
    INHALING;
}


enum class DisplayedSkinPart(val bit: Byte) {
    CAPE(0x01),
    JACKET(0x02),
    LEFT_SLEEVE(0x04),
    RIGHT_SLEEVE(0x08),
    LEFT_PANTS(0x10),
    RIGHT_PANTS(0x20),
    HAT(0x40),
    UNUSED(0x80.toByte())
}

fun Collection<DisplayedSkinPart>.getBitMask(): Byte {
    var out: Byte = 0x00
    for (part in this) {
        out = (out or part.bit).toByte()
    }
    return out
}

enum class Direction {
    DOWN,
    UP,
    NORTH,
    SOUTH,
    WEST,
    EAST
}

fun Entity.getDirection(noPitch: Boolean = false): Direction = getDirection(this.location, noPitch)

fun getDirection(location: Location, noPitch: Boolean = false): Direction =
    getDirection(location.yaw, location.pitch, noPitch)

fun getDirection(yaw: Float, pitch: Float, noPitch: Boolean = false): Direction {
    val normalizedYaw = if (yaw < 0) yaw + 360 else yaw % 360

    if (!noPitch) {
        return when {
            pitch < -45 -> Direction.UP
            pitch > 45 -> Direction.DOWN
            else -> throw IllegalStateException("Invalid pitch value: $yaw")
        }
    }

    return when {
        normalizedYaw < 45 || normalizedYaw >= 315 -> Direction.SOUTH
        normalizedYaw in 45.0..135.0 -> Direction.WEST
        normalizedYaw in 135.0..225.0 -> Direction.NORTH
        normalizedYaw in 225.0..315.0 -> Direction.EAST

        else -> throw IllegalStateException("Invalid yaw value: $yaw")
    }
}

fun Direction.getYawAndPitch(): Pair<Float, Float> {
    return when (this) {
        Direction.NORTH -> 180f to 0f
        Direction.SOUTH -> 0f to 0f
        Direction.EAST -> 270f to 0f
        Direction.WEST -> 90f to 0f
        Direction.UP -> 0f to -90f
        Direction.DOWN -> 0f to 90f
    }
}

fun Direction.getOpposite(): Direction {
    return when (this) {
        Direction.DOWN -> Direction.UP
        Direction.SOUTH -> Direction.NORTH
        Direction.WEST -> Direction.EAST
        Direction.NORTH -> Direction.SOUTH
        else -> Direction.WEST
    }
}

fun Direction.toVector3f(): Vector3f {
    return when (this) {
        Direction.NORTH -> Vector3f(0f, 0f, -1f)
        Direction.SOUTH -> Vector3f(0f, 0f, 1f)
        Direction.EAST -> Vector3f(1f, 0f, 0f)
        Direction.WEST -> Vector3f(-1f, 0f, 0f)
        Direction.UP -> Vector3f(0f, 1f, 0f)
        Direction.DOWN -> Vector3f(0f, -1f, 0f)
        else -> Vector3f(0f, 0f, 0f)
    }
}


fun yawPitchToVector(yaw: Float, pitch: Float): Vector3d {
    val yawRad = yaw * PI / 180.0
    val pitchRad = pitch * PI / 180.0

    val x = cos(yawRad) * cos(pitchRad)
    val y = -sin(pitchRad) // Negative because in Minecraft, up is negative y
    val z = sin(yawRad) * cos(pitchRad)

    return Vector3d(x, y, z)
}