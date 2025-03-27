package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.networktypes.writeList
import io.github.dockyardmc.apis.serverlinks.ServerLink

class ClientboundServerLinksPacket(serverLinks: Collection<ServerLink>) : ClientboundPacket() {

    init {
        buffer.writeList(serverLinks, ServerLink::write)
    }
}

