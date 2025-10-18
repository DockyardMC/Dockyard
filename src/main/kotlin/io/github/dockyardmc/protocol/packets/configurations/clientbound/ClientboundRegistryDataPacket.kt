package io.github.dockyardmc.protocol.packets.configurations.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.Registry

class ClientboundRegistryDataPacket(val registry: Registry<*>) : ClientboundPacket() {

    init {
        registry.write(buffer)
    }
}

