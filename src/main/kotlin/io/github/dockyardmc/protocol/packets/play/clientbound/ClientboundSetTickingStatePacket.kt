package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetTickingStatePacket(
    tickRate: Int,
    isFrozen: Boolean,
) : ClientboundPacket() {

    init {
        data.writeFloat(tickRate.toFloat())
        data.writeBoolean(isFrozen)
    }

}