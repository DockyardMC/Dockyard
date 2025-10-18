package io.github.dockyardmc.world.waypoint

import io.github.dockyardmc.codec.ExtraCodecs
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.tide.types.Either
import io.github.dockyardmc.world.chunk.ChunkPos
import io.netty.buffer.ByteBuf
import java.util.*

data class WaypointData(val id: Either<UUID, String>, val icon: Icon, val target: Target) : NetworkWritable {

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.either(StreamCodec.UUID, StreamCodec.STRING), WaypointData::id,
            Icon.STREAM_CODEC, WaypointData::icon,
            Target.STREAM_CODEC, WaypointData::target,
            ::WaypointData
        )
    }

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }

    data class Icon(val style: String, val color: CustomColor?) : NetworkWritable {

        companion object : NetworkReadable<Icon> {

            val STREAM_CODEC = StreamCodec.of(
                StreamCodec.STRING, Icon::style,
                ExtraCodecs.CUSTOM_COLOR_STREAM.optional(), Icon::color,
                ::Icon
            )

            const val DEFAULT_STYLE = "minecraft:default"
            val DEFAULT = Icon(DEFAULT_STYLE, null)

            override fun read(buffer: ByteBuf): Icon {
                return STREAM_CODEC.read(buffer)
            }
        }

        override fun write(buffer: ByteBuf) {
            STREAM_CODEC.write(buffer, this)
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

            val STREAM_CODEC = object : StreamCodec<Target> {

                override fun write(buffer: ByteBuf, value: Target) {
                    value.write(buffer)
                }

                override fun read(buffer: ByteBuf): Target {
                    return Companion.read(buffer)
                }

            }

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

        constructor(location: Location) : this(location.toVector3())

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