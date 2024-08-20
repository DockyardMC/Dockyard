package io.github.dockyardmc.utils

import io.netty.buffer.ByteBuf
import kotlin.math.cos
import kotlin.math.sin

// I hate quaternions whoever invented this should think about themselves
data class Quaternion(
    val x: Float,
    val y: Float,
    val z: Float,
    val w: Float
) {
    companion object {
        fun fromAxis(axisX: Float, axisY: Float, axisZ: Float): Quaternion {
            val xRadians = MathUtils.degreesToRadians(axisX)
            val yRadians = MathUtils.degreesToRadians(axisY)
            val zRadians = MathUtils.degreesToRadians(axisZ)

            val qx = Quaternion(sin(xRadians / 2), 0f, 0f, cos(xRadians / 2))
            val qy = Quaternion(0f, sin(yRadians / 2), 0f, cos(yRadians / 2))
            val qz = Quaternion(0f, 0f, sin(zRadians / 2), cos(zRadians / 2))

            val q = MathUtils.multiplyQuaternions(MathUtils.multiplyQuaternions(qz, qy), qx)

            return q
        }

        fun fromAxis(vector3f: Vector3f): Quaternion = fromAxis(vector3f.x, vector3f.y, vector3f.z)
    }
}

fun ByteBuf.writeQuaternion(quaternion: Quaternion) {
    this.writeFloat(quaternion.x)
    this.writeFloat(quaternion.y)
    this.writeFloat(quaternion.z)
    this.writeFloat(quaternion.w)
}

