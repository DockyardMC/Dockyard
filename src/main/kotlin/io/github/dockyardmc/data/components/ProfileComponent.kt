package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf
import java.util.*

class ProfileComponent(val name: String?, val uuid: UUID?, val properties: List<Property>) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeOptional(name, ByteBuf::writeString)
        buffer.writeOptional(uuid, ByteBuf::writeUUID)
        buffer.writeList(properties, Property::write)
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


    data class Property(val name: String, val value: String, val signature: String?) : NetworkWritable {

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