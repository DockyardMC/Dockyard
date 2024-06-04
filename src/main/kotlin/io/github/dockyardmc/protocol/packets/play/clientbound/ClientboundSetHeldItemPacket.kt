package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetHeldItemPacket(slot: Int): ClientboundPacket(0x51) {

    init {
        data.writeByte(slot)
    }

}