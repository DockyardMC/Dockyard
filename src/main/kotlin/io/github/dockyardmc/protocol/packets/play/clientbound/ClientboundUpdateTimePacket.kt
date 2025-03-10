package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundUpdateTimePacket(
    val worldAge: Long,
    val time: Long,
    val isFrozen: Boolean
) : ClientboundPacket() {

    init {
        data.writeLong(worldAge)
        data.writeLong(time)
        data.writeBoolean(isFrozen)
    }
}