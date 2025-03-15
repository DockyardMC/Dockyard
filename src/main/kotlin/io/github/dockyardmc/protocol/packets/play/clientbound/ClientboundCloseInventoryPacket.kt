package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundCloseInventoryPacket(id: Int) : ClientboundPacket() {
    init {
        buffer.writeByte(id)
    }
}