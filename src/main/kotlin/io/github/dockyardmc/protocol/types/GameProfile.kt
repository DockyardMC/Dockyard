package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import java.util.*

data class GameProfile(val uuid: UUID, val username: String, val properties: List<Property> = listOf()) : NetworkWritable {

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }

    companion object : NetworkReadable<GameProfile> {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.UUID, GameProfile::uuid,
            StreamCodec.STRING, GameProfile::username,
            Property.STREAM_CODEC.list(), GameProfile::properties,
            ::GameProfile
        )

        val CODEC = StructCodec.of(
            "uuid", Codec.UUID, GameProfile::uuid,
            "username", Codec.STRING, GameProfile::username,
            "properties", Property.CODEC.list(), GameProfile::properties,
            ::GameProfile
        )

        override fun read(buffer: ByteBuf): GameProfile {
            return STREAM_CODEC.read(buffer)
        }
    }

    init {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(username.length <= 16) { "Username cannot be more than 16 characters" }
    }

    @Serializable
    data class Property(val name: String, val value: String, val signature: String? = null) : NetworkWritable {

        companion object : NetworkReadable<Property> {
            val STREAM_CODEC = StreamCodec.of(
                StreamCodec.STRING, Property::name,
                StreamCodec.STRING, Property::value,
                StreamCodec.STRING.optional(), Property::signature,
                ::Property
            )

            val CODEC = StructCodec.of(
                "name", Codec.STRING, Property::name,
                "value", Codec.STRING, Property::value,
                "signature", Codec.STRING.optional(), Property::signature,
                ::Property
            )

            override fun read(buffer: ByteBuf): Property {
                return STREAM_CODEC.read(buffer)
            }
        }

        override fun write(buffer: ByteBuf) {
            STREAM_CODEC.write(buffer, this)
        }
    }

}