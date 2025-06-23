package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.writeOptional
import io.netty.buffer.ByteBuf
import java.util.*

data class GameProfile(val uuid: UUID, val username: String, val properties: List<Property> = listOf()) : NetworkWritable {

    override fun write(buffer: ByteBuf) {
        buffer.writeUUID(uuid)
        buffer.writeString(username)
        buffer.writeList(properties, Property::write)
    }

    companion object : NetworkReadable<GameProfile> {
        override fun read(buffer: ByteBuf): GameProfile {
            return GameProfile(buffer.readUUID(), buffer.readString(), buffer.readList(Property::read))
        }
    }

    init {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(username.length <= 16) { "Username cannot be more than 16 characters" }
    }

    data class Property(val name: String, val value: String, val signature: String? = null) : NetworkWritable {

        companion object : NetworkReadable<Property> {
            override fun read(buffer: ByteBuf): Property {
                return Property(buffer.readString(), buffer.readString(), buffer.readOptional(ByteBuf::readString))
            }
        }

        override fun write(buffer: ByteBuf) {
            buffer.writeString(name)
            buffer.writeString(value)
            buffer.writeOptional(signature, ByteBuf::writeString)
        }
    }

}