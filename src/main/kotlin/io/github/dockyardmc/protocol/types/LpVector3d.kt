package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.extentions.writeByte
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.maths.absMax
import io.github.dockyardmc.maths.ceilLong
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

data class LpVector3d(val x: Double, val y: Double, val z: Double) {

    constructor(vector3d: Vector3d) : this(vector3d.x, vector3d.y, vector3d.y)
    constructor(vector3d: Vector3f) : this(vector3d.x.toDouble(), vector3d.y.toDouble(), vector3d.y.toDouble())
    constructor(vector3d: Vector3) : this(vector3d.x.toDouble(), vector3d.y.toDouble(), vector3d.y.toDouble())

    companion object {
        private const val DATA_BITS_MASK = 0b111111111111111
        private const val MAX_QUANTIZED_VALUE = 32766.0
        private const val SCALE_BITS_MASK: Long = 0b11
        private const val CONTINUATION_FLAG: Long = 4
        private const val X_OFFSET = 3
        private const val Y_OFFSET = 18
        private const val Z_OFFSET = 33
        private const val ABS_MAX_VALUE = 1.7179869183E10
        private const val ABS_MIN_VALUE = 3.051944088384301E-5

        private fun sanitize(value: Double): Double {
            return if (value.isNaN()) 0.0 else Math.clamp(value, -ABS_MAX_VALUE, ABS_MAX_VALUE)
        }

        private fun pack(value: Double): Long {
            return Math.round((value * 0.5 + 0.5) * MAX_QUANTIZED_VALUE)
        }

        private fun unpack(value: Long): Double {
            return (value and DATA_BITS_MASK.toLong()).toDouble().coerceAtMost(MAX_QUANTIZED_VALUE) * 2.0 / MAX_QUANTIZED_VALUE - 1.0
        }

        val STREAM_CODEC = object : StreamCodec<LpVector3d> {

            override fun write(buffer: ByteBuf, value: LpVector3d) {
                val x = sanitize(value.x)
                val y = sanitize(value.y)
                val z = sanitize(value.z)
                val max = absMax(x, absMax(y, z))
                if (max < ABS_MIN_VALUE) {
                    buffer.writeByte(0)
                } else {
                    val i = ceilLong(max)
                    val hasContinuationBit = (i and SCALE_BITS_MASK) != i
                    val flags = if (hasContinuationBit) i and SCALE_BITS_MASK or CONTINUATION_FLAG else i
                    val px = pack(x / i) shl X_OFFSET
                    val py = pack(y / i) shl Y_OFFSET
                    val pz = pack(z / i) shl Z_OFFSET
                    val packed = flags or px or py or pz
                    buffer.writeByte(packed.toByte())
                    buffer.writeByte((packed shr 8).toByte())
                    buffer.writeByte((packed shr 16).toByte())
                    if (hasContinuationBit) {
                        buffer.writeVarInt((i shr 2).toInt())
                    }
                }
            }

            override fun read(buffer: ByteBuf): LpVector3d {
                TODO("Not yet implemented")
            }

        }
    }
}