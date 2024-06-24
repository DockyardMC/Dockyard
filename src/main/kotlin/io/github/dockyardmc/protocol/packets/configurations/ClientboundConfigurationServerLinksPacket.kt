package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.Link
import io.github.dockyardmc.protocol.packets.play.clientbound.writeLinks

@WikiVGEntry("Server Links (configuration)")
@ClientboundPacketInfo(0x10, ProtocolState.CONFIGURATION)
class ClientboundConfigurationServerLinksPacket(
    links: Collection<Link>
): ClientboundPacket() {
    init {
        writeLinks(links, data)
    }
}