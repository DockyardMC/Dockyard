package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.protocol.*
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.utils.toIntArray
import io.netty.buffer.ByteBuf
import java.util.*

data class ProfileComponent(val name: String?, val uuid: UUID?, val properties: List<Property>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeOptional(name, ByteBuf::writeString)
        buffer.writeOptional(uuid, ByteBuf::writeUUID)
        buffer.writeList(properties, Property::write)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            optional("name", name, CRC32CHasher::ofString)
            optional("uuid", uuid?.toIntArray(), CRC32CHasher::ofIntArray)
            defaultStructList("properties", properties, listOf(), Property::hashStruct)
        }
    }

    companion object : NetworkReadable<ProfileComponent> {
        override fun read(buffer: ByteBuf): ProfileComponent {
            return ProfileComponent(
                buffer.readOptional(ByteBuf::readString),
                buffer.readOptional(ByteBuf::readUUID),
                buffer.readList(Property::read)
            )
        }
    }

    data class Property(val name: String, val value: String, val signature: String?) : NetworkWritable, DataComponentHashable {

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                static("name", CRC32CHasher.ofString(name))
                static("value", CRC32CHasher.ofString(value))
                optional("signature", signature, CRC32CHasher::ofString)
            }
        }

        override fun write(buffer: ByteBuf) {
            buffer.writeString(name)
            buffer.writeString(value)
            buffer.writeOptional(signature, ByteBuf::writeString)
        }

        companion object : NetworkReadable<Property> {
            override fun read(buffer: ByteBuf): Property {
                return Property(buffer.readString(), buffer.readString(), buffer.readOptional(ByteBuf::readString))
            }
        }
    }
}