package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.resourcepack.ResourcePack
import io.github.dockyardmc.tide.Codecs
import java.util.*

data class ClientboundPlayRemoveResourcepackPacket(val uuid: UUID?) : ClientboundPacket() {

    constructor(resourcePack: ResourcePack) : this(resourcePack.uuid)

    init {
        Codecs.UUID.optional().writeNetwork(buffer, uuid)
    }
}