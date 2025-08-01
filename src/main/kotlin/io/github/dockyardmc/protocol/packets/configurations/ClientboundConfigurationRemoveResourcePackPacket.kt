package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.resourcepack.ResourcePack
import io.github.dockyardmc.tide.Codecs
import java.util.*

data class ClientboundConfigurationRemoveResourcePackPacket(val uuid: UUID? = null) : ClientboundPacket() {

    constructor(resourcePack: ResourcePack) : this(resourcePack.uuid)

    init {
        Codecs.UUID.optional().writeNetwork(buffer, uuid)
    }
}