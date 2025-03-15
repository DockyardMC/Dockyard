package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundClearTitlePacket(val reset: Boolean) : ClientboundPacket() {
    init {
        buffer.writeBoolean(reset)
    }
}