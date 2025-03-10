package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetWorldBorderWarningDistance(distance: Int) : ClientboundPacket() {

    init {
        data.writeVarInt(distance)
    }

}