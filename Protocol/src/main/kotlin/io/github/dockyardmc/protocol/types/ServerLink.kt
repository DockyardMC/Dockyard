package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.protocol.ProtocolWritable
import io.github.dockyardmc.protocol.writers.*
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf

sealed interface ServerLink: ProtocolWritable {
    val url: String

    companion object {
        fun read(buffer: ByteBuf): ServerLink {
            val isDefault = buffer.readBoolean()
            return if(isDefault) DefaultServerLink.read(buffer) else CustomServerLink.read(buffer)
        }
    }
}
data class DefaultServerLink(val type: Type, override val url: String): ServerLink {

    override fun write(buffer: ByteBuf) {
        buffer.writeBoolean(true)
        buffer.writeVarInt(type.ordinal)
        buffer.writeString(url)
    }

    companion object {
        fun read(buffer: ByteBuf): DefaultServerLink {
            return DefaultServerLink(
                buffer.readEnum<Type>(),
                buffer.readString()
            )
        }
    }

    enum class Type {
        BUG_REPORT,
        COMMUNITY_GUIDELINES,
        SUPPORT,
        STATUS,
        FEEDBACK,
        COMMUNITY,
        WEBSITE,
        FORUMS,
        NEWS,
        ANNOUNCEMENTS
    }
}

data class CustomServerLink(val label: Component, override val url: String): ServerLink {
    constructor(label: String, url: String) : this(label.toComponent(), url)

    override fun write(buffer: ByteBuf) {
        buffer.writeBoolean(false)
        buffer.writeTextComponent(label)
        buffer.writeString(url)
    }

    companion object {
        fun read(buffer: ByteBuf): CustomServerLink {
            return CustomServerLink(
                buffer.readTextComponent(),
                buffer.readString()
            )
        }
    }
}


