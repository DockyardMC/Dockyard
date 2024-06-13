package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundFinishConfigurationPacket: ClientboundPacket(2, ProtocolState.CONFIGURATION) {
}