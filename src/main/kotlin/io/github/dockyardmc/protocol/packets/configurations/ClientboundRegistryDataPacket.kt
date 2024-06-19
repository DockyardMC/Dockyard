package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.registry.Registry
import io.github.dockyardmc.registry.writeRegistry

@WikiVGEntry("Registry Data")
class ClientboundRegistryDataPacket(registry: Registry): ClientboundPacket(0x07, ProtocolState.CONFIGURATION) {

    init {
        data.writeRegistry(registry)
    }
}