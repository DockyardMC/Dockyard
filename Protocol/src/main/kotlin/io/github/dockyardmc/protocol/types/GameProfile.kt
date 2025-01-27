package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.writers.*
import io.netty.buffer.ByteBuf
import java.lang.IllegalArgumentException
import java.util.UUID

data class GameProfile(
    val uuid: UUID,
    val name: String,
    val properties: List<Property>
): NetworkWritable {

    companion object {
        const val MAX_PROPERTIES = 1024

        fun read(buffer: ByteBuf): GameProfile {
            return GameProfile(
                buffer.readUUID(),
                buffer.readString(),
                buffer.readList<Property>(Property.Companion::read)
            )
        }
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeUUID(uuid)
        buffer.writeString(name)
        buffer.writeList<Property>(properties, Property::write)
    }

    init {
        if(name.isEmpty()) throw IllegalArgumentException("Name cannot be empty")
        if(name.length > 16) throw IllegalArgumentException("Name length cannot be more than 16 characters")
        if(properties.size > MAX_PROPERTIES) throw IllegalArgumentException("Properties exceed the maximum amount of $MAX_PROPERTIES")
    }


    data class Property(var name: String, var value: String, var signature: String?): NetworkWritable {

        override fun write(buffer: ByteBuf) {
            buffer.writeString(name)
            buffer.writeString(value)
            buffer.writeOptional(signature, ByteBuf::writeString)
        }

        companion object {
            fun read(buffer: ByteBuf): Property {
                return Property(
                    buffer.readString(),
                    buffer.readString(),
                    buffer.readOptional<String>(ByteBuf::readString)
                )
            }
        }
    }
}