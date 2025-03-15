package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundDisconnectPacket(reason: String) : ClientboundPacket() {

    init {
        buffer.writeTextComponent(reason)
    }

}