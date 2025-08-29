package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.resourcepack.ResourcePack

data class ClientboundConfigurationAddResourcePackPacket(val resourcePack: ResourcePack) : ClientboundPacket() {

    init {
        ResourcePack.STREAM_CODEC.write(buffer, resourcePack)
    }
}