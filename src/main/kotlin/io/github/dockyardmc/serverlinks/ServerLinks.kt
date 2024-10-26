package io.github.dockyardmc.serverlinks

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf

object ServerLinks {
    val links: MutableList<ServerLink> = mutableListOf()
}

sealed interface ServerLink {
    val url: String
}

data class DefaultServerLink(val type: DefaultServerLinkType, override val url: String): ServerLink

data class CustomServerLink(val label: Component, override val url: String): ServerLink {
    constructor(label: String, url: String) : this(label.toComponent(), url)
}

enum class DefaultServerLinkType {
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

fun ByteBuf.writeServerLinks(serverLinks: MutableList<ServerLink>) {
    this.writeVarInt(serverLinks.size)

    serverLinks.forEach {
        when(it) {
            is DefaultServerLink -> {
                this.writeBoolean(true)
                this.writeVarInt(it.type.ordinal)
            }
            is CustomServerLink -> {
                this.writeBoolean(false)
                this.writeNBT(it.label.toNBT())
            }
        }
        this.writeString(it.url)
    }
}