package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundRegistryDataPacket(nbt: String): ClientboundPacket(5) {

    init {
        data.writeUtf(nbt)
    }
}