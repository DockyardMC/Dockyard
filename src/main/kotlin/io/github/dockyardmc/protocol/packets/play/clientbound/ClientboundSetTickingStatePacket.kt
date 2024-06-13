package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundSetTickingStatePacket(tickRate: Float, isFrozen: Boolean): ClientboundPacket(0x6E, ProtocolState.PLAY) {

    init {
        data.writeFloat(tickRate)
        data.writeBoolean(isFrozen)
    }

}