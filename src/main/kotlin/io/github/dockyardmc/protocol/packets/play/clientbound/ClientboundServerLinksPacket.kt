package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.serverlinks.ServerLink
import io.github.dockyardmc.serverlinks.writeServerLinks

class ClientboundServerLinksPacket(serverLinks: MutableList<ServerLink>) : ClientboundPacket() {
    init {
        data.writeServerLinks(serverLinks)
    }
}

