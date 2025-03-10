package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.serverlinks.ServerLink
import io.github.dockyardmc.serverlinks.writeServerLinks

class ClientboundConfigurationServerLinksPacket(
    serverLinks: MutableList<ServerLink>
): ClientboundPacket() {

    init {
        data.writeServerLinks(serverLinks)
    }

}