package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.Component

class ClientboundSystemChatMessagePacket(component: Component, isActionBar: Boolean): ClientboundPacket(0x69) {

    init {
        data.writeNBT(component.toNBT())
        data.writeBoolean(isActionBar)
    }
}