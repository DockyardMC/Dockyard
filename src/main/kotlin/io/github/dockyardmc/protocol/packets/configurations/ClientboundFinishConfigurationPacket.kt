package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Finish Configuration")
class ClientboundFinishConfigurationPacket: ClientboundPacket(0x03, ProtocolState.CONFIGURATION) {
}