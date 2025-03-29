package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.apis.serverlinks.ServerLink

class ClientboundConfigurationServerLinksPacket(
    serverLinks: Collection<ServerLink>
) : ClientboundPacket() {

    init {
        buffer.writeList(serverLinks, ServerLink::write)
    }
}