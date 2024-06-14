package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.registry.vanilla.Registry
import io.github.dockyardmc.registry.vanilla.writeRegistry
import org.jglrxavpok.hephaistos.nbt.NBT

@WikiVGEntry("Registry Data")
class ClientboundRegistryDataPacket(registry: Registry): ClientboundPacket(0x07, ProtocolState.CONFIGURATION) {

    init {
        data.writeRegistry(registry)
    }
}