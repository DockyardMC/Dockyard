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
import io.github.dockyardmc.serverlinks.ServerLink
import io.github.dockyardmc.serverlinks.writeServerLinks
import io.netty.buffer.ByteBuf

@WikiVGEntry("Server Links")
@ClientboundPacketInfo(0x7B, ProtocolState.PLAY)
class ClientboundServerLinksPacket(serverLinks: MutableList<ServerLink>): ClientboundPacket() {
    init {
        data.writeServerLinks(serverLinks)
    }
}

