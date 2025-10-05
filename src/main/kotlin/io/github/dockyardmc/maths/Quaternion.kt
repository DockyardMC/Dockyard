package io.github.dockyardmc.maths

import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// I hate quaternions whoever invented this should think about themselves
data class Quaternion(
    val x: Float,
    val y: Float,
    val z: Float,
    val w: Float
) {

    fun rotate(vector: Vector3f): Vector3f {
        val q = this // The quaternion representing the rotation
        val p = Quaternion(0f, vector.x, vector.y, vector.z)

        val rotatedP = multiplyQuaternions(multiplyQuaternions(q, p), q).conjugate()

        return Vector3f(rotatedP.x, rotatedP.y, rotatedP.z)
    }

    fun conjugate(): Quaternion = Quaternion(this.w, -this.x, -this.y, -this.z)

    companion object {

        val DEFAULT = Quaternion(0f, 0f, 0f, 1f)

        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.FLOAT, Quaternion::x,
            StreamCodec.FLOAT, Quaternion::y,
            StreamCodec.FLOAT, Quaternion::z,
            StreamCodec.FLOAT, Quaternion::w,
            ::Quaternion
        )

        fun fromAxis(axisX: Float, axisY: Float, axisZ: Float): Quaternion {
            val xRadians = degreesToRadians(axisX)
            val yRadians = degreesToRadians(axisY)
            val zRadians = degreesToRadians(axisZ)

            val qx = Quaternion(sin(xRadians / 2), 0f, 0f, cos(xRadians / 2))
            val qy = Quaternion(0f, sin(yRadians / 2), 0f, cos(yRadians / 2))
            val qz = Quaternion(0f, 0f, sin(zRadians / 2), cos(zRadians / 2))

            val q = multiplyQuaternions(multiplyQuaternions(qz, qy), qx)

            return q
        }

        fun fromAxisAngle(axis: Vector3f, angleDegrees: Double): Quaternion {
            val angleRadians = angleDegrees * PI / 180.0 // Convert degrees to radians
            val halfAngle = angleRadians / 2.0
            val sinHalfAngle = sin(halfAngle).toFloat()
            val cosHalfAngle = cos(halfAngle).toFloat()

            return Quaternion(
                cosHalfAngle,
                axis.x * sinHalfAngle,
                axis.y * sinHalfAngle,
                axis.z * sinHalfAngle
            )
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

