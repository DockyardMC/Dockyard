package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.serverlinks.ServerLink
import io.github.dockyardmc.serverlinks.writeServerLinks

@WikiVGEntry("Server Links (configuration)")
@ClientboundPacketInfo(0x10, ProtocolState.CONFIGURATION)
class ClientboundConfigurationServerLinksPacket(
    serverLinks: MutableList<ServerLink>
): ClientboundPacket() {

    init {
        data.writeServerLinks(serverLinks)
    }

}