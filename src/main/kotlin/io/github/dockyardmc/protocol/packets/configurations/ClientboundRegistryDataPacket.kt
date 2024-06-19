package io.github.dockyardmc.protocol.packets.configurations

import cz.lukynka.prettylog.log
import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.registry.Registry
import io.github.dockyardmc.registry.writeRegistry

@WikiVGEntry("Registry Data")
@ClientboundPacketInfo(0x07, ProtocolState.CONFIGURATION)
class ClientboundRegistryDataPacket(registry: Registry): ClientboundPacket() {

    init {
        log("Sent registry $registry")
        data.writeRegistry(registry)
    }
}