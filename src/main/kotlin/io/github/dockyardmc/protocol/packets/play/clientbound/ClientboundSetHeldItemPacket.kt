package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundSetHeldItemPacket(slot: Int): ClientboundPacket(0x51, ProtocolState.PLAY) {

    init {
        data.writeByte(slot)
    }

}