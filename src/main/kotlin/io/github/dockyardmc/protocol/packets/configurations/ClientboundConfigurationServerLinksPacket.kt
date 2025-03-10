package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.writeList
import io.github.dockyardmc.serverlinks.ServerLink

class ClientboundConfigurationServerLinksPacket(
    serverLinks: Collection<ServerLink>
) : ClientboundPacket() {

    init {
        data.writeList(serverLinks, ServerLink::write)
    }
}