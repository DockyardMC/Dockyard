package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.Component

class ClientboundDisconnectPacket(reason: Component): ClientboundPacket(0x1B) {

    init {
        data.writeNBT(reason.toNBT())
    }

}