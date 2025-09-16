package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.resourcepack.ResourcePack
import io.github.dockyardmc.tide.stream.StreamCodec
import java.util.*

data class ClientboundConfigurationRemoveResourcePackPacket(val uuid: UUID? = null) : ClientboundPacket() {

    constructor(resourcePack: ResourcePack) : this(resourcePack.uuid)

    init {
        StreamCodec.UUID.optional().write(buffer, uuid)
    }
}