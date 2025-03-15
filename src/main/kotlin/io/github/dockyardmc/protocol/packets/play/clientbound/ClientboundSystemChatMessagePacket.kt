package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.Component

class ClientboundSystemChatMessagePacket(
    component: Component,
    isActionBar: Boolean,
) : ClientboundPacket() {

    init {
        buffer.writeNBT(component.toNBT())
        buffer.writeBoolean(isActionBar)
    }
}