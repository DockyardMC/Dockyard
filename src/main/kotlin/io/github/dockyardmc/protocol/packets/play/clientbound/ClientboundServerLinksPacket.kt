package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf

enum class LinkType {
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

sealed interface Link {
    val url: String
}

data class CommonLink(val type: LinkType, override val url: String): Link

data class CustomLink(val label: Component, override val url: String): Link {
    constructor(label: String, url: String) : this(label.toComponent(), url)
}

@WikiVGEntry("Server Links")
@ClientboundPacketInfo(0x7B, ProtocolState.PLAY)
class ClientboundServerLinksPacket(links: Collection<Link>): ClientboundPacket() {
    init {
        writeLinks(links, data)
    }
}

fun writeLinks(links: Collection<Link>, buffer: ByteBuf) {
    buffer.writeVarInt(links.size)

    links.forEach {
        if (it is CommonLink) {
            buffer.writeBoolean(true)
            buffer.writeVarInt(it.type.ordinal)
        } else if (it is CustomLink) {
            buffer.writeBoolean(false)
            buffer.writeNBT(it.label.toNBT())
        }

        buffer.writeUtf(it.url)
    }
}