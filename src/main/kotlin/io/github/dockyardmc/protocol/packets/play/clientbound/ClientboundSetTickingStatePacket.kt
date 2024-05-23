package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetTickingStatePacket(tickRate: Float, isFrozen: Boolean): ClientboundPacket(0x6E) {

    init {
        data.writeFloat(tickRate)
        data.writeBoolean(isFrozen)
    }

}