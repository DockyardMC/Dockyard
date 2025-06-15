package io.github.dockyardmc.world

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.types.Either
import io.github.dockyardmc.protocol.types.writeEither
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.world.chunk.ChunkPos
import io.netty.buffer.ByteBuf
import java.util.*

data class Waypoint(val id: Either<UUID, String>, val icon: Icon, val target: Target) : NetworkWritable {

    override fun write(buffer: ByteBuf) {
        buffer.writeEither(id, ByteBuf::writeUUID, ByteBuf::writeString)
        icon.write(buffer)
        target.write(buffer)
    }

    data class Icon(val style: String, val color: CustomColor?) : NetworkWritable {

        companion object : NetworkReadable<Icon> {

            const val DEFAULT_STYLE = "minecraft:default"
            val DEFAULT = Icon(DEFAULT_STYLE, null)

            override fun read(buffer: ByteBuf): Icon {
                return Icon(buffer.readString(), buffer.readOptional(ByteBuf::readCustomColor))
            }
        }

        override fun write(buffer: ByteBuf) {
            buffer.writeString(style)
            buffer.writeOptional(color, CustomColor::writePackedInt)
        }
    }

    sealed interface Target : NetworkWritable {

        val type: Type

        enum class Type {
            EMPTY, VEC3, CHUNK, AZIMUTH
        }

        override fun write(buffer: ByteBuf) {
            buffer.writeEnum(type)
            this.writeInner(buffer)
        }

        fun writeInner(buffer: ByteBuf)

        companion object : NetworkReadable<Target> {

            override fun read(buffer: ByteBuf): Target {
                val type = buffer.readEnum<Type>()
                return when (type) {
                    Type.EMPTY -> Empty.read(buffer)
                    Type.VEC3 -> Vec3.read(buffer)
                    Type.CHUNK -> Chunk.read(buffer)
                    Type.AZIMUTH -> Azimuth.read(buffer)
                }
            }
        }
    }

    class Empty : Target {
        override fun writeInner(buffer: ByteBuf) {}
        override val type: Target.Type = Target.Type.EMPTY

        companion object : NetworkReadable<Empty> {
            override fun read(buffer: ByteBuf): Empty {
                return Empty()
            }
        }
    }

    data class Vec3(val vector3: Vector3) : Target {

        override val type: Target.Type = Target.Type.VEC3

        override fun writeInner(buffer: ByteBuf) {
            vector3.write(buffer)
        }

        companion object : NetworkReadable<Vec3> {
            override fun read(buffer: ByteBuf): Vec3 {
                return Vec3(Vector3.read(buffer))
            }
        }
    }

    data class Chunk(val chunkPos: ChunkPos) : Target {

        override val type: Target.Type = Target.Type.CHUNK

        override fun writeInner(buffer: ByteBuf) {
            chunkPos.write(buffer)
        }

        companion object : NetworkReadable<Chunk> {
            override fun read(buffer: ByteBuf): Chunk {
                return Chunk(ChunkPos.read(buffer))
            }
        }
    }

    data class Azimuth(val angle: Float) : Target {

        override val type: Target.Type = Target.Type.AZIMUTH

        override fun writeInner(buffer: ByteBuf) {
            buffer.writeFloat(angle)
        }

        companion object : NetworkReadable<Azimuth> {
            override fun read(buffer: ByteBuf): Azimuth {
                return Azimuth(buffer.readFloat())
            }
        }
    }
}