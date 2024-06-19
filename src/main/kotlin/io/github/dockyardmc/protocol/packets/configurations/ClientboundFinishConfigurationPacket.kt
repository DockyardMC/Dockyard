package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Finish Configuration")
@ClientboundPacketInfo(0x03, ProtocolState.CONFIGURATION)
class ClientboundFinishConfigurationPacket: ClientboundPacket() {
}