package io.github.dockyardmc.apis.serverlinks

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

data class DefaultServerLink(val type: Type, override val url: String): ServerLink {

    override fun write(buffer: ByteBuf) {
        buffer.writeBoolean(true)
        buffer.writeVarInt(type.ordinal)
        buffer.writeString(url)
    }

    companion object: NetworkReadable<DefaultServerLink> {
        override fun read(buffer: ByteBuf): DefaultServerLink {
            return DefaultServerLink(buffer.readVarIntEnum<Type>(), buffer.readString())
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