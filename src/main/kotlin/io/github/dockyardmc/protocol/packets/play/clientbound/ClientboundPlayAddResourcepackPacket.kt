package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.resourcepack.ResourcePack

class ClientboundPlayAddResourcepackPacket(resourcepack: ResourcePack) : ClientboundPacket() {

    init {
        ResourcePack.STREAM_CODEC.writeNetwork(buffer, resourcepack)
    }
}